from fastapi import FastAPI
from pydantic import BaseModel
import re
from roles import ROLE_PROFILES

app = FastAPI()


class ResumeRequest(BaseModel):
    resume_text: str
    target_role: str | None = None


# ----------------------------
# Normalization
# ----------------------------

def normalize_text(text: str) -> str:
    text = text.lower()
    text = re.sub(r'[^a-z0-9+#./ ]', ' ', text)
    return text


# ----------------------------
# Skill Extraction
# ----------------------------

def extract_all_possible_skills():
    skill_set = set()

    for role in ROLE_PROFILES.values():
        categories = role["categories"]

        for skills in categories.values():
            for skill in skills:
                skill_set.add(skill.lower())

    return skill_set


ALL_SKILLS = extract_all_possible_skills()


def extract_skills(text: str):
    normalized = normalize_text(text)
    found = set()

    for skill in ALL_SKILLS:
        if skill in normalized:
            found.add(skill)

    return list(found)


# ----------------------------
# Role Scoring Engine
# ----------------------------

def score_role(extracted_skills: list, role_profile: dict):

    categories = role_profile["categories"]
    core_categories = role_profile["core_categories"]

    core_scores = []
    non_core_scores = []

    missing_by_category = {}
    matched_skills = []

    for category_name, skills in categories.items():

        skills_lower = [s.lower() for s in skills]

        matched = [s for s in skills_lower if s in extracted_skills]
        missing = [s for s in skills_lower if s not in extracted_skills]

        # ---- CORE CATEGORY LOGIC (THRESHOLD BASED) ----
        if category_name in core_categories:
            if len(matched) >= 1:
                category_score = 1
            else:
                category_score = 0
            core_scores.append(category_score)

        # ---- NON-CORE CATEGORY LOGIC (FRACTIONAL) ----
        else:
            if skills_lower:
                category_score = len(matched) / len(skills_lower)
                non_core_scores.append(category_score)

        if missing:
            missing_by_category[category_name] = missing

        matched_skills.extend(matched)

    core_avg = sum(core_scores) / len(core_scores) if core_scores else 0
    non_core_avg = sum(non_core_scores) / len(non_core_scores) if non_core_scores else 0

    final_score = round((core_avg * 0.7 + non_core_avg * 0.3) * 100)

    top_missing = []
    for skills in missing_by_category.values():
        top_missing.extend(skills)

    top_missing = top_missing[:5]

    return {
        "score": final_score,
        "matched_skills": list(set(matched_skills)),
        "missing_by_category": missing_by_category,
        "top_missing_skills": top_missing
    }


# ----------------------------
# Main Endpoint
# ----------------------------

@app.post("/analyze")
async def analyze_resume(request: ResumeRequest):

    extracted_skills = extract_skills(request.resume_text)

    role_results = {}

    for role_name, profile in ROLE_PROFILES.items():
        role_results[role_name] = score_role(extracted_skills, profile)

    # Sort roles by score descending
    sorted_roles = sorted(
        role_results.items(),
        key=lambda x: x[1]["score"],
        reverse=True
    )

    top_3 = sorted_roles[:3]

    response = {
        "best_fit_roles": [
            {
                "role": role_name,
                "score": data["score"],
                "matched_skills": data["matched_skills"],
                "missing_by_category": data["missing_by_category"],
                "top_missing_skills": data["top_missing_skills"]
            }
            for role_name, data in top_3
        ],
        "all_role_scores": {
            role: data["score"] for role, data in role_results.items()
        }
    }

    # If target_role provided → return deep analysis for it
    if request.target_role and request.target_role in ROLE_PROFILES:
        response["target_role_analysis"] = {
            "role": request.target_role,
            **role_results[request.target_role]
        }

    return response