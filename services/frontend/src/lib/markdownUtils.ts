import * as emoji from 'node-emoji';

export function convertEmojis(markdown: string | undefined): string {
	if (!markdown) return '';

	// Use regex to find all :xyz: patterns
	const regex = /:(\w+):/g;
	return markdown.replace(regex, (match, p1) => {
		// Replace each :xyz: with the emoji
		return emoji.get(p1) || match;
	});
}
