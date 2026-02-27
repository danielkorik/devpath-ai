ROLE_PROFILES = {

    # ---------------------------
    # WEB & APPLICATION ROLES
    # ---------------------------

    "backend_developer": {
        "core_categories": ["core", "frameworks", "databases"],
        "categories": {
            "core": ["java", "python", "c#", "go"],
            "frameworks": ["spring boot", "django", "fastapi", "node.js"],
            "databases": ["sql", "postgresql", "mysql", "mongodb"],
            "api_design": ["rest"],
            "devops": ["docker", "aws", "gcp"],
            "architecture": ["microservices"]
        }
    },

    "frontend_developer": {
        "core_categories": ["core", "frameworks"],
        "categories": {
            "core": ["javascript", "typescript"],
            "frameworks": ["react", "angular", "vue"],
            "styling": ["css", "html"],
            "tooling": ["vite", "webpack"],
            "testing": ["jest", "cypress"]
        }
    },

    "full_stack_developer": {
        "core_categories": ["core", "frontend", "backend"],
        "categories": {
            "core": ["javascript", "typescript", "python"],
            "frontend": ["react", "angular"],
            "backend": ["node.js", "spring boot", "fastapi"],
            "database": ["sql", "mongodb"],
            "devops": ["docker"]
        }
    },

    "mobile_engineer": {
        "core_categories": ["ios", "android", "cross_platform"],
        "categories": {
            "ios": ["swift", "swiftui"],
            "android": ["kotlin"],
            "cross_platform": ["react native", "flutter"],
            "backend_integration": ["rest"]
        }
    },

    # ---------------------------
    # INFRASTRUCTURE & SECURITY
    # ---------------------------

    "devops_engineer": {
        "core_categories": ["core", "cloud"],
        "categories": {
            "core": ["linux", "docker", "kubernetes"],
            "cloud": ["aws", "gcp", "azure"],
            "ci_cd": ["github actions", "gitlab ci", "jenkins"],
            "iac": ["terraform"],
            "scripting": ["bash", "python"]
        }
    },

    "sre_engineer": {
        "core_categories": ["core"],
        "categories": {
            "core": ["linux", "kubernetes", "monitoring"],
            "observability": ["prometheus", "grafana"],
            "incident_management": ["incident response"]
        }
    },

    "cybersecurity_engineer": {
        "core_categories": ["core"],
        "categories": {
            "core": ["linux", "networking", "python"],
            "offensive": ["penetration testing", "metasploit"],
            "defensive": ["siem", "firewall"],
            "cloud_security": ["iam"]
        }
    },

    # ---------------------------
    # DATA & AI
    # ---------------------------

    "data_engineer": {
        "core_categories": ["core", "pipelines"],
        "categories": {
            "core": ["python", "sql"],
            "pipelines": ["spark", "etl"],
            "orchestration": ["airflow"],
            "storage": ["data warehouse"]
        }
    },

    "ml_engineer": {
        "core_categories": ["core", "frameworks"],
        "categories": {
            "core": ["python"],
            "frameworks": ["pytorch", "tensorflow"],
            "mlops": ["mlflow"],
            "deployment": ["docker"]
        }
    },

    # ---------------------------
    # SYSTEMS & SPECIALIZED
    # ---------------------------

    "systems_engineer": {
        "core_categories": ["core"],
        "categories": {
            "core": ["c++", "c"],
            "systems": ["linux", "operating systems"],
            "debugging": ["gdb"]
        }
    },

    "embedded_engineer": {
        "core_categories": ["core"],
        "categories": {
            "core": ["c", "c++"],
            "hardware": ["microcontrollers"],
            "protocols": ["spi", "i2c"]
        }
    },

    "game_developer": {
        "core_categories": ["core"],
        "categories": {
            "core": ["c++", "c#"],
            "engines": ["unity", "unreal engine"],
            "graphics": ["opengl"]
        }
    },

    "solution_architect": {
        "core_categories": ["design"],
        "categories": {
            "design": ["system design"],
            "cloud": ["aws", "azure", "gcp"],
            "security": ["oauth2"]
        }
    }
}