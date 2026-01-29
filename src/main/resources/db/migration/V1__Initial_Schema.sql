-- =============================================
-- V1__Initial_Schema.sql
-- Schema inicial completo e padronizado
-- Student Portal API
-- =============================================

-- =============================================
-- EXTENSÕES
-- =============================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================
-- TABELA: users
-- Usuários do sistema (Admin, Professor, Estudante)
-- =============================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    registration VARCHAR(255) UNIQUE,
    access_enable BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_role CHECK (role IN ('SUPER_USER', 'ADMIN', 'TEACHER', 'STUDENT'))
);

-- Índices
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_registration ON users(registration);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- =============================================
-- TABELA: courses
-- Cursos disponíveis no sistema
-- =============================================
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_courses_status CHECK (status IN ('DRAFT', 'SCHEDULED', 'ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_courses_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

-- Índices
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_start_date ON courses(start_date);
CREATE INDEX idx_courses_created_at ON courses(created_at DESC);

-- =============================================
-- TABELA: tasks
-- Tarefas/Atividades dos cursos
-- =============================================
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    course_id UUID NOT NULL,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_tasks_status CHECK (status IN ('PENDING', 'SUBMITTED', 'LATE', 'GRADED', 'RETURNED'))
);

-- Índices
CREATE INDEX idx_tasks_course_id ON tasks(course_id);
CREATE INDEX idx_tasks_created_by ON tasks(created_by);
CREATE INDEX idx_tasks_deadline ON tasks(deadline);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

-- =============================================
-- TABELA: questions
-- Perguntas feitas pelos usuários
-- =============================================
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    user_id UUID NOT NULL,
    answer_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_questions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_questions_answer_count CHECK (answer_count >= 0)
);

-- Índices
CREATE INDEX idx_questions_user_id ON questions(user_id);
CREATE INDEX idx_questions_created_at ON questions(created_at DESC);
CREATE INDEX idx_questions_answer_count ON questions(answer_count DESC);

-- =============================================
-- TABELA: answers
-- Respostas às perguntas
-- =============================================
CREATE TABLE answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    user_id UUID NOT NULL,
    question_id UUID NOT NULL,
    is_accepted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_answers_user_id ON answers(user_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_answers_is_accepted ON answers(is_accepted) WHERE is_accepted = true;
CREATE INDEX idx_answers_created_at ON answers(created_at DESC);

-- =============================================
-- TABELA: materials
-- Materiais de estudo (PDFs, vídeos, etc)
-- =============================================
CREATE TABLE materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'OTHER',
    filename VARCHAR(255) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    user_id UUID NOT NULL,
    course_id UUID,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    downloads BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_materials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    CONSTRAINT chk_materials_category CHECK (category IN ('PDF', 'VIDEO', 'ARTICLE', 'PRESENTATION', 'DOCUMENT', 'SPREADSHEET', 'IMAGE', 'AUDIO', 'COMPRESSED', 'OTHER')),
    CONSTRAINT chk_materials_downloads CHECK (downloads >= 0)
);

-- Índices
CREATE INDEX idx_materials_user_id ON materials(user_id);
CREATE INDEX idx_materials_course_id ON materials(course_id);
CREATE INDEX idx_materials_category ON materials(category);
CREATE INDEX idx_materials_downloads ON materials(downloads DESC);
CREATE INDEX idx_materials_created_at ON materials(created_at DESC);

-- =============================================
-- TABELA: payments
-- Pagamentos dos estudantes
-- =============================================
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    payment_date DATE,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    payment_method VARCHAR(20),
    transaction_id VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_payments_status CHECK (status IN ('PENDENTE', 'PAGO', 'ATRASADO', 'CANCELADO', 'REEMBOLSADO')),
    CONSTRAINT chk_payments_method CHECK (payment_method IS NULL OR payment_method IN ('PIX', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_SLIP', 'BANK_TRANSFER')),
    CONSTRAINT chk_payments_amount CHECK (amount > 0)
);

-- Índices
CREATE INDEX idx_payments_student_id ON payments(student_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_due_date ON payments(due_date);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

-- =============================================
-- TABELA: enrollments (NOVA)
-- Matrículas de estudantes em cursos
-- =============================================
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    grade NUMERIC(4,2),
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uk_enrollments_student_course UNIQUE (student_id, course_id),
    CONSTRAINT chk_enrollments_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'DROPPED', 'SUSPENDED')),
    CONSTRAINT chk_enrollments_grade CHECK (grade IS NULL OR (grade >= 0 AND grade <= 10))
);

-- Índices
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_created_at ON enrollments(created_at DESC);

-- =============================================
-- TABELA: task_submissions (NOVA)
-- Entregas de tarefas pelos estudantes
-- =============================================
CREATE TABLE task_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL,
    student_id UUID NOT NULL,
    content TEXT,
    file_url VARCHAR(500),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    grade NUMERIC(4,2),
    feedback TEXT,
    graded_by UUID,
    graded_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_submissions_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_submissions_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_submissions_graded_by FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uk_task_submissions_task_student UNIQUE (task_id, student_id),
    CONSTRAINT chk_task_submissions_status CHECK (status IN ('SUBMITTED', 'GRADED', 'RETURNED')),
    CONSTRAINT chk_task_submissions_grade CHECK (grade IS NULL OR (grade >= 0 AND grade <= 10))
);

-- Índices
CREATE INDEX idx_task_submissions_task_id ON task_submissions(task_id);
CREATE INDEX idx_task_submissions_student_id ON task_submissions(student_id);
CREATE INDEX idx_task_submissions_status ON task_submissions(status);
CREATE INDEX idx_task_submissions_submitted_at ON task_submissions(submitted_at DESC);

-- =============================================
-- FUNÇÕES E TRIGGERS
-- =============================================

-- Função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_courses_updated_at
    BEFORE UPDATE ON courses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_questions_updated_at
    BEFORE UPDATE ON questions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_answers_updated_at
    BEFORE UPDATE ON answers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_materials_updated_at
    BEFORE UPDATE ON materials
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_enrollments_updated_at
    BEFORE UPDATE ON enrollments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_task_submissions_updated_at
    BEFORE UPDATE ON task_submissions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Função para atualizar answer_count em questions
CREATE OR REPLACE FUNCTION update_answer_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE questions SET answer_count = answer_count + 1 WHERE id = NEW.question_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE questions SET answer_count = answer_count - 1 WHERE id = OLD.question_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ language 'plpgsql';

-- Trigger para manter answer_count sincronizado
CREATE TRIGGER trigger_update_answer_count
    AFTER INSERT OR DELETE ON answers
    FOR EACH ROW EXECUTE FUNCTION update_answer_count();

-- =============================================
-- DADOS INICIAIS (Opcional)
-- =============================================

-- Criar usuário administrador padrão (senha: admin123 - TROCAR EM PRODUÇÃO!)
-- A senha está hasheada com BCrypt
INSERT INTO users (name, email, password, role, access_enable)
VALUES (
    'Administrador',
    'admin@studentportal.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqBuBjZa3lSvBhKGjnwLz0b9D7fO2', -- admin123
    'SUPER_USER',
    true
) ON CONFLICT (email) DO NOTHING;

-- =============================================
-- COMENTÁRIOS NAS TABELAS
-- =============================================

COMMENT ON TABLE users IS 'Usuários do sistema (administradores, professores e estudantes)';
COMMENT ON TABLE courses IS 'Cursos disponíveis na plataforma';
COMMENT ON TABLE tasks IS 'Tarefas e atividades dos cursos';
COMMENT ON TABLE questions IS 'Perguntas feitas pelos usuários no fórum';
COMMENT ON TABLE answers IS 'Respostas às perguntas do fórum';
COMMENT ON TABLE materials IS 'Materiais de estudo (arquivos, vídeos, etc)';
COMMENT ON TABLE payments IS 'Registros de pagamentos dos estudantes';
COMMENT ON TABLE enrollments IS 'Matrículas dos estudantes nos cursos';
COMMENT ON TABLE task_submissions IS 'Entregas de tarefas pelos estudantes';