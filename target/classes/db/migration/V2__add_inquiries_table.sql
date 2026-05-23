-- V2: Add inquiries table to replace Redis cache

CREATE TABLE inquiries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inquiry_id VARCHAR(36) NOT NULL UNIQUE,
    merchant_id VARCHAR(100) NOT NULL,
    merchant_name VARCHAR(255),
    terminal_id VARCHAR(100),
    city VARCHAR(100),
    fixed_amount DECIMAL(18, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inquiries_inquiry_id ON inquiries(inquiry_id);
