import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    iterations: 100,
};

export default function () {

    const payload = JSON.stringify({
        userId: `user-${__VU}-${__ITER}`,
        ticketTypeId: 1,
        quantity: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const response = http.post(
        'http://localhost:8080/api/v1/reservations',
        payload,
        params
    );

    check(response, {
        'request succeeded': (r) => r.status === 201,
    });
}