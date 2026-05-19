local expireTime = tonumber(ARGV[2]) -- 检查 expireTime 是否有效，如果无效直接返回
if not expireTime or expireTime <= 0 then
     return 0  -- expireTime 无效，返回 0
end
if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then  -- expireTime 有效，继续检查是否能设置锁
     return redis.call('expire', KEYS[1], expireTime)  -- 如果 setnx 成功，设置过期时间
else
     return 2 -- 如果锁已经存在，返回 2
end