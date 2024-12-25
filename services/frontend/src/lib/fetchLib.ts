import type { ServerLoadEvent } from '@sveltejs/kit';
import type { LayoutRouteId, RouteParams } from '../routes/$types';

export function fetchProxy(
	req: ServerLoadEvent<RouteParams, {}, LayoutRouteId>,
	url: string,
	options: RequestInit = {}
): Promise<Response> {
	// Extract the EDITMD_SESSION cookie from req.cookies
	const editmdSessionCookie = req.cookies.get('EDITMD_SESSION');

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

	// Send the request to the URL with updated options using fetch
	return fetch(url, options);
}
