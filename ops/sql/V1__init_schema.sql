-- ===== V1__init_schema.sql =====

-- 1) users
CREATE TABLE users (
  id            BIGSERIAL PRIMARY KEY,
  email         TEXT NOT NULL,
  password_hash TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_users_email_ci ON users (LOWER(email));

-- 2) projects
CREATE TABLE projects (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name        TEXT NOT NULL,
  color_hex   VARCHAR(7),
  archived_at TIMESTAMPTZ,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_projects_color_hex CHECK (color_hex IS NULL OR color_hex ~ '^#[0-9A-Fa-f]{6}$')
);

-- 3) tasks
CREATE TABLE tasks (
  id                  BIGSERIAL PRIMARY KEY,
  user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  project_id          BIGINT REFERENCES projects(id) ON DELETE SET NULL,
  parent_task_id      BIGINT REFERENCES tasks(id) ON DELETE SET NULL,
  title               TEXT NOT NULL,
  description         TEXT,
  status              TEXT NOT NULL DEFAULT 'todo'
                        CHECK (status IN ('todo','in_progress','done','archived')),
  priority            SMALLINT NOT NULL DEFAULT 0,
  due_date            DATE,
  order_index         NUMERIC(10,3),
  estimated_pomodoros INT NOT NULL DEFAULT 0,
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_tasks_user_status_proj ON tasks(user_id, status, project_id);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_parent  ON tasks(parent_task_id);

-- 4) tags
CREATE TABLE tags (
  id        BIGSERIAL PRIMARY KEY,
  user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name      TEXT NOT NULL,
  color_hex VARCHAR(7),
  CONSTRAINT chk_tags_color_hex CHECK (color_hex IS NULL OR color_hex ~ '^#[0-9A-Fa-f]{6}$')
);
CREATE UNIQUE INDEX ux_tags_user_name_ci ON tags (user_id, LOWER(name));

-- 5) task_tags
CREATE TABLE task_tags (
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  tag_id  BIGINT NOT NULL REFERENCES tags(id)  ON DELETE CASCADE,
  PRIMARY KEY (task_id, tag_id)
);
CREATE INDEX idx_task_tags_tag ON task_tags(tag_id);

-- 6) pomodoro_sessions
CREATE TABLE pomodoro_sessions (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  task_id         BIGINT REFERENCES tasks(id) ON DELETE SET NULL,
  planned_minutes INT NOT NULL DEFAULT 25,
  focus_seconds   INT NOT NULL DEFAULT 0,
  started_at      TIMESTAMPTZ NOT NULL,
  ended_at        TIMESTAMPTZ,
  state           TEXT NOT NULL DEFAULT 'running'
                    CHECK (state IN ('running','completed','aborted')),
  notes           TEXT
);
CREATE INDEX idx_sessions_user_started ON pomodoro_sessions(user_id, started_at DESC);
CREATE INDEX idx_sessions_task         ON pomodoro_sessions(task_id);

-- 7) session_events
CREATE TABLE session_events (
  id         BIGSERIAL PRIMARY KEY,
  session_id BIGINT NOT NULL REFERENCES pomodoro_sessions(id) ON DELETE CASCADE,
  event_type TEXT NOT NULL
               CHECK (event_type IN ('start','pause','resume','finish','break_start','break_end','interrupt')),
  at         TIMESTAMPTZ NOT NULL DEFAULT now(),
  meta       JSONB
);
CREATE INDEX idx_events_session_at ON session_events(session_id, at);

-- 8) user_settings
CREATE TABLE user_settings (
  user_id          BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  focus_len_min    INT NOT NULL DEFAULT 25,
  short_break_min  INT NOT NULL DEFAULT 5,
  long_break_min   INT NOT NULL DEFAULT 15,
  long_break_every INT NOT NULL DEFAULT 4
);