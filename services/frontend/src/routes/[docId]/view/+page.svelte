<script lang="ts">
	import type { PageData } from './$types';
	import { page } from '$app/state';
	import Header from '$lib/components/header.svelte';
	import MarkdownViewer from '$lib/components/markdownViewer.svelte';
	import { goto } from '$app/navigation';

	let { data }: { data: PageData } = $props();
	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	// generate long text
	let text = $state('');

	for (let i = 0; i < 1000; i++) {
		if (i % 2 == 0) {
			text += '**';
		}

		text += 'Hello world!';

		if (i % 2 == 0) {
			text += '** ';
		} else {
			text += ' ';
		}
	}

	function editDocument() {
		goto('./');
	}
</script>

<div class="pageContainer bg-background">
	<Header {user}>
		{#snippet center()}
			<h1 class="pageTitle">View Document {page.params.docId}</h1>
		{/snippet}
		{#snippet right()}
			<button class="rounded-md bg-foreground-10 px-4 py-1" onclick={editDocument}>Edit</button>
		{/snippet}
	</Header>
	<MarkdownViewer content={text} class=""></MarkdownViewer>
</div>

<style>
	.pageContainer {
		width: 100%;
		height: 100dvh;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.pageTitle {
		@apply truncate;
	}
</style>
