import { APPLY } from '@api/endPoint';
import { http, HttpResponse } from 'msw';
import applyForm from '../applyForm.json';
import { NotFoundError, Success } from './response';

const applyHandlers = [
  http.get(`${APPLY}/:postId`, async ({ request }) => {
    const url = new URL(request.url);
    const postId = url.pathname.split('/').pop();

    if (!postId) {
      return NotFoundError();
    }

    return HttpResponse.json(applyForm);
  }),

  http.post(`${APPLY}/:postId/submit`, async ({ request }) => {
    const url = new URL(request.url);
    const postId = url.pathname.split('/').pop();

    if (!postId) {
      return NotFoundError();
    }

    return Success();
  }),
];

export default applyHandlers;