export async function parseBadRequest(resp: Response): Promise<{ [key: string]: string }> {
	if (resp.status === 400) {
		let body = await resp.json();

		let errors: { [key: string]: string } = {};

		if (body && body.errors) {
			body.errors.forEach((error: any) => {
				errors[error.field] = {
					error: error.defaultMessage
				};
			});
		}

		return errors;
	}

	return {};
}

export function deepMerge(target: any, source: any) {
	for (const key in source) {
		if (source[key] && typeof source[key] === 'object' && !Array.isArray(source[key])) {
			if (!target[key]) target[key] = {};
			deepMerge(target[key], source[key]);
		} else {
			target[key] = source[key];
		}
	}
	return target;
}
