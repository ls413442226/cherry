-- KEYS[1] = refreshKey
-- ARGV[1] = expectedSessionJson

local val = redis.call('GET', KEYS[1])

if not val then
    return 0
end

if val == ARGV[1] then
    return -1
end

redis.call('DEL', KEYS[1])

return 1