-- Ensure schema exists and search_path is set
CREATE SCHEMA IF NOT EXISTS public;
SET search_path = public;

-- =========================
-- USERS
-- =========================
CREATE TABLE public.users (
                            id            BIGSERIAL PRIMARY KEY,
                            email         TEXT        NOT NULL,
                            password_hash TEXT        NOT NULL,
                            full_name     TEXT,                       -- display name: optional
                            username      TEXT       NOT NULL,                -- display username: optional, unique (case-insensitive index below)
                            created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Unique, case-insensitive email
CREATE UNIQUE INDEX ux_users_email_ci
  ON public.users (lower(email));

-- Optional but recommended: unique, case-insensitive username
CREATE UNIQUE INDEX ux_users_username_ci
  ON public.users (lower(username));

-- =========================
-- PROJECTS
-- =========================
CREATE TABLE public.projects (
                               id          BIGSERIAL PRIMARY KEY,
                               user_id     BIGINT      NOT NULL,
                               name        TEXT        NOT NULL,
                               color_hex   VARCHAR(7),
                               archived_at TIMESTAMPTZ,
                               created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                               updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                               CONSTRAINT chk_projects_color_hex
                                 CHECK (
                                   color_hex IS NULL OR color_hex ~ '^#[0-9A-Fa-f]{6}$'
),
  CONSTRAINT projects_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- =========================
-- TAGS
-- =========================
CREATE TABLE public.tags (
                           id        BIGSERIAL PRIMARY KEY,
                           user_id   BIGINT NOT NULL,
                           name      TEXT   NOT NULL,
                           color_hex VARCHAR(7),
                           CONSTRAINT chk_tags_color_hex
                             CHECK (
                               color_hex IS NULL OR color_hex ~ '^#[0-9A-Fa-f]{6}$'
),
  CONSTRAINT tags_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Unique tag name per user (case-insensitive)
CREATE UNIQUE INDEX ux_tags_user_name_ci
  ON public.tags (user_id, lower(name));

-- =========================
-- TASKS
-- =========================
CREATE TABLE public.tasks (
                            id                   BIGSERIAL PRIMARY KEY,
                            user_id              BIGINT      NOT NULL,
                            project_id           BIGINT,
                            parent_task_id       BIGINT,
                            title                TEXT        NOT NULL,
                            description          TEXT,
                            status               TEXT        NOT NULL DEFAULT 'todo',
                            priority             SMALLINT    NOT NULL DEFAULT 0,
                            due_date             DATE,
                            order_index          NUMERIC(10,3),
                            estimated_pomodoros  INTEGER     NOT NULL DEFAULT 0,
                            created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
                            total_focus_seconds  INTEGER     NOT NULL DEFAULT 0,
                            completed_sessions   INTEGER     NOT NULL DEFAULT 0,
                            CONSTRAINT tasks_status_check
                              CHECK (status IN ('todo','in_progress','done','archived')),
                            CONSTRAINT tasks_user_id_fkey
                              FOREIGN KEY (user_id)    REFERENCES public.users(id)    ON DELETE CASCADE,
                            CONSTRAINT tasks_project_id_fkey
                              FOREIGN KEY (project_id) REFERENCES public.projects(id) ON DELETE SET NULL,
                            CONSTRAINT tasks_parent_task_id_fkey
                              FOREIGN KEY (parent_task_id) REFERENCES public.tasks(id) ON DELETE SET NULL
);

CREATE INDEX idx_tasks_parent
  ON public.tasks (parent_task_id);

CREATE INDEX idx_tasks_project
  ON public.tasks (project_id);

CREATE INDEX idx_tasks_total_focus
  ON public.tasks (total_focus_seconds);

CREATE INDEX idx_tasks_user_status_proj
  ON public.tasks (user_id, status, project_id);

-- =========================
-- USER SETTINGS (1-1 by PK)
-- =========================
CREATE TABLE public.user_settings (
                                    user_id         BIGINT      PRIMARY KEY,
                                    focus_len_min   INTEGER     NOT NULL DEFAULT 25,
                                    short_break_min INTEGER     NOT NULL DEFAULT 5,
                                    long_break_min  INTEGER     NOT NULL DEFAULT 15,
                                    long_break_every INTEGER    NOT NULL DEFAULT 4,
                                    CONSTRAINT user_settings_user_id_fkey
                                      FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- =========================
-- POMODORO SESSIONS
-- =========================
CREATE TABLE public.pomodoro_sessions (
                                        id             BIGSERIAL PRIMARY KEY,
                                        user_id        BIGINT      NOT NULL,
                                        task_id        BIGINT,
                                        planned_minutes INTEGER    NOT NULL DEFAULT 25,
                                        focus_seconds   INTEGER    NOT NULL DEFAULT 0,
                                        started_at      TIMESTAMPTZ NOT NULL,
                                        ended_at        TIMESTAMPTZ,
                                        state           TEXT        NOT NULL DEFAULT 'running',
                                        notes           TEXT,
                                        CONSTRAINT pomodoro_sessions_state_check
                                          CHECK (state IN ('running','completed','aborted')),
                                        CONSTRAINT pomodoro_sessions_user_id_fkey
                                          FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
                                        CONSTRAINT pomodoro_sessions_task_id_fkey
                                          FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE SET NULL
);

CREATE INDEX idx_sessions_task
  ON public.pomodoro_sessions (task_id);

-- Desc index içeren bileşik (user_id, started_at DESC)
CREATE INDEX idx_sessions_user_started
  ON public.pomodoro_sessions (user_id, started_at DESC);

-- =========================
-- SESSION EVENTS
-- =========================
CREATE TABLE public.session_events (
                                     id         BIGSERIAL PRIMARY KEY,
                                     session_id BIGINT      NOT NULL,
                                     event_type TEXT        NOT NULL,
                                     "at"       TIMESTAMPTZ NOT NULL DEFAULT now(),
                                     meta       JSONB,
                                     CONSTRAINT session_events_event_type_check
                                       CHECK (event_type IN ('start','pause','resume','finish','break_start','break_end','interrupt')),
                                     CONSTRAINT session_events_session_id_fkey
                                       FOREIGN KEY (session_id) REFERENCES public.pomodoro_sessions(id) ON DELETE CASCADE
);

CREATE INDEX idx_events_session_at
  ON public.session_events (session_id, "at");

-- =========================
-- TASK_TAGS (M:N)
-- =========================
CREATE TABLE public.task_tags (
                                task_id BIGINT NOT NULL,
                                tag_id  BIGINT NOT NULL,
                                CONSTRAINT task_tags_pkey PRIMARY KEY (task_id, tag_id),
                                CONSTRAINT task_tags_task_id_fkey
                                  FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE,
                                CONSTRAINT task_tags_tag_id_fkey
                                  FOREIGN KEY (tag_id)  REFERENCES public.tags(id)  ON DELETE CASCADE
);

CREATE INDEX idx_task_tags_tag
  ON public.task_tags (tag_id);
