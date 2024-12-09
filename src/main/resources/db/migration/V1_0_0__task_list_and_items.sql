CREATE TABLE task_list (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT uq_demo_task_list_name UNIQUE (name)
);

CREATE TABLE task_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_list_id UUID NOT NULL,
    description TEXT NOT NULL,
    sort_order INT NOT NULL,
    is_complete BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    completed_at BIGINT DEFAULT NULL,
    CONSTRAINT fk_demo_task_item_list FOREIGN KEY (task_list_id) REFERENCES task_list(id) ON DELETE CASCADE
);

CREATE INDEX idx_demo_task_item_list ON task_item (task_list_id, sort_order, created_at);