<script lang="ts">
	import type { PageData } from './$types';
	import { page } from '$app/state';
	import Header from '$lib/components/header.svelte';
	import MarkdownViewer from '$lib/components/markdownViewer.svelte';
	import { goto } from '$app/navigation';

	let { data }: { data: PageData } = $props();
	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	let documentState = $state({
		title: data.document?.title || 'Untitled',
		content: data.document?.content || ''
	});

	function editDocument() {
		goto('./');
	}
</script>

<div class="pageContainer bg-background">
	<Header {user}>
		{#snippet center()}
			<h1 class="pageTitle">{documentState.title}</h1>
		{/snippet}
		{#snippet right()}
			<button class="rounded-md bg-foreground-10 px-4 py-1" onclick={editDocument}>Edit</button>
		{/snippet}
	</Header>
		<MarkdownViewer content={documentState.content} inContainer={true}></MarkdownViewer>
</div>

<style>
	.pageContainer {
		width: 100%;
		height: 100dvh;
		overflow: hidden;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.pageTitle {
		@apply truncate;
	}
</style>
