import type { ServerLoadEvent } from '@sveltejs/kit';
import type { LayoutRouteId, RouteParams } from '../routes/$types';

export class SessionError extends Error {
	constructor(message: string) {
		super(message);
		this.name = 'SessionError';
	}
}

/**
 * Fetch a URL, adding the EDITMD_SESSION cookie from the request to the headers
 * @param req The request object
 * @param url The URL to fetch
 * @param options Fetch options
 */
export async function fetchProxy(
	req: ServerLoadEvent<RouteParams, {}, LayoutRouteId>,
	url: string,
	options: RequestInit = {}
): Promise<Response> {
	// Extract the EDITMD_SESSION cookie from req.cookies
	const editmdSessionCookie = req.cookies.get('EDITMD_SESSION');

	if(editmdSessionCookie === undefined) {
		throw new SessionError('EDITMD_SESSION cookie not found');
	}

	// Ensure options.headers is an instance of Headers, or convert to it
	if (!(options.headers instanceof Headers)) {
		options.headers = new Headers(options.headers);
	}

	// Add X-CSRF-Protection header to options.headers
	options.headers.set('X-CSRF-Protection', '1');

	// If the EDITMD_SESSION cookie exists, add it to the Cookie header
	if (editmdSessionCookie) {
		const existingCookie = options.headers.get('Cookie') || '';
		options.headers.set('Cookie', `${existingCookie}; EDITMD_SESSION=${editmdSessionCookie}`);
	}

	var resp = await fetch(url, options);

	// if unauthorized, remove the EDITMD_SESSION cookie
	if (resp.status === 401) {
		req.cookies.delete('EDITMD_SESSION', {
			path: '/',
		});
		throw new SessionError('Unauthorized');
	}

	return resp;
}
