local inventory = tonumber(
    redis.call('GET', KEYS[1]) or '0'
)

local quantity = tonumber(ARGV[1])

local eventId = ARGV[2]
local reservationId = ARGV[3]
local userId = ARGV[4]
local ticketTypeId = ARGV[5]
local amount = ARGV[6]

if quantity <= 0 then
    return -1
end

if inventory < quantity then

    redis.call(
        'XADD',
        KEYS[2],
        '*',
        'eventType',
        'INVENTORY_UNAVAILABLE',
        'eventId',
        eventId,
        'reservationId',
        reservationId,
        'userId',
        userId,
        'ticketTypeId',
        ticketTypeId,
        'quantity',
        quantity,
        'amount',
        amount,
        'reason',
        'INSUFFICIENT_INVENTORY'
    )

    return 0
end

redis.call(
    'DECRBY',
    KEYS[1],
    quantity
)

redis.call(
    'XADD',
    KEYS[2],
    '*',
    'eventType',
    'INVENTORY_RESERVED',
    'eventId',
    eventId,
    'reservationId',
    reservationId,
    'userId',
    userId,
    'ticketTypeId',
    ticketTypeId,
    'quantity',
    quantity,
    'amount',
    amount
)

return 1