<script lang="ts">
	import DocumentCard from '$lib/components/documentCard.svelte';
	import Header from '$lib/components/header.svelte';

	let { user } = $props();
</script>

<div class="pageContainer bg-background">
	<Header {user}></Header>
	<div class="contentContainer container mx-auto p-4">
		<h1 class="text-2xl font-bold">Your Documents</h1>
		<p class="mb-2 text-lg text-foreground-80">
			Here are the documents you have created or have been shared with you.
		</p>

		<h2 class="mb-2 text-xl font-semibold">Created by you</h2>
		<div class="documentGrid mb-4 grid gap-2">
			<DocumentCard
				document={{
					title: 'Document 1 aaaaaaa a a adasd asd as d',
					lastEdit: 'Last edited 2 days ago'
				}}
			/>
			<DocumentCard document={{ title: 'Document 2', lastEdit: 'Last edited 5 days ago' }} />
			<DocumentCard document={{ title: 'Document 3', lastEdit: 'Last edited 1 week ago' }} />
		</div>

		<h2 class="mb-2 text-xl font-semibold">Shared with you</h2>
		<div class="documentGrid grid gap-2">
			<DocumentCard document={{ title: 'Document 4', lastEdit: 'Last edited 3 days ago' }} />
			<DocumentCard document={{ title: 'Document 5', lastEdit: 'Last edited 1 week ago' }} />
			<DocumentCard document={{ title: 'Document 6', lastEdit: 'Last edited 2 weeks ago' }} />
		</div>
	</div>
</div>

<style>
	.documentGrid {
		grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
	}

	.pageContainer {
		width: 100%;
		min-height: 100dvh;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.contentContainer {
		height: 100%;
		width: 100%;
	}
</style>
