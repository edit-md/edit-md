<script lang="ts">
	import DocumentCard from '$lib/components/documentCard.svelte';
	import Profile from '$lib/components/profile.svelte';

	let { user } = $props();
</script>

<div class="pageContainer bg-background">
	<div class="header bg-background-alt">
		<h1 class="text-xl"># edit.md</h1>
		<Profile {user} />
	</div>
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
		height: 100dvh;
		display: grid;
		grid-template-rows: 3rem 1fr;
	}

	.contentContainer {
		height: 100%;
		width: 100%;
	}

	.header {
		top: 0;
		display: flex;
		justify-content: space-between;
		align-items: center;
		position: sticky;
		z-index: 100;
		padding: 1rem;
	}
</style>
