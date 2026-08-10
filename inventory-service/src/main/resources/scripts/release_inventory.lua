local processed = redis.call('EXISTS', KEYS[2])

if processed == 1 then
    return 0
end

redis.call('INCRBY', KEYS[1], ARGV[1])

redis.call('SET', KEYS[2], '1')

return 1