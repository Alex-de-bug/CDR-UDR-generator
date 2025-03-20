CREATE INDEX idx_call_start_time ON call(start_time);
CREATE INDEX idx_call_caller ON call(caller);
CREATE INDEX idx_call_receiver ON call(receiver);
CREATE INDEX idx_call_call_type ON call(call_type);