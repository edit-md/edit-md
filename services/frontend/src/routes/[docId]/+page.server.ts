import { fetchProxy, SessionError } from '$lib/fetchLib';
import { redirect, type ServerLoadEvent } from '@sveltejs/kit';
import type { PageServerLoad, RouteParams } from './$types';
import {env} from "$env/dynamic/private";
import type { LayoutRouteId } from '../$types';

export const load: PageServerLoad = async (req) => {
	let data = {
		document: undefined,
		files: [],
		previewActive: req.cookies.get('previewActive') === 'true'
	};

	const documentServiceHost = env.EDITMD_DOCUMENT_SERVICE_HOST;
	const fileServiceHost = env.EDITMD_FILE_SERVICE_HOST;

	try {
		data.document = await fetchData(req, `${documentServiceHost}/api/documents/${req.params.docId}`);
	} catch (e) {
		if (e instanceof SessionError) {
			// ignored
		} else if (e instanceof DOMException && e.name === 'AbortError') {
			console.log('Request to document-service timed out');
		} else {
			console.log('Unexpected error:', e);
			redirect(302, '/');
		}
	}

	try {
		data.files = await fetchData(req, `${fileServiceHost}/api/files?doc=${req.params.docId}`);
	} catch (e) {
		if (e instanceof SessionError) {
			// ignored
		} else if (e instanceof DOMException && e.name === 'AbortError') {
			console.log('Request to document-service timed out');
		} else {
			console.log('Unexpected error:', e);
			redirect(302, '/');
		}
	}

	return data;
};

function fetchData(
	req: ServerLoadEvent<RouteParams, {}, LayoutRouteId>,
	url: string
): Promise<any> {
	return new Promise(async (resolve, reject) => {
		try {
			const controller = new AbortController();
			const timeoutId = setTimeout(() => controller.abort(), 500);

			let resp = await fetchProxy(req, url, {
				signal: controller.signal,
				unauthenticated: true,
			});

			clearTimeout(timeoutId);

			if (resp.ok) {
				let data = await resp.json();
				resolve(data);
				return;
			}
		} catch (e) {
			reject(e);
		}

		reject(new Error('Failed to fetch data'));
	});
}
