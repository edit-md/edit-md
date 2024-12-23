<script lang="ts">
	import DocumentCard from "$lib/components/documentCard.svelte";
	import Profile from "$lib/components/profile.svelte";

    let { user } = $props();
</script>

<div class="pageContainer bg-zinc-900">
	<div class="header bg-zinc-950">
		<h1 class="text-xl text-white"># edit.md</h1>
		<button
			class="flex h-8 w-8 items-center justify-center rounded-md text-white"
			aria-label="Open preview"
		>
		</button>
        <Profile {user} />
	</div>
	<div class="contentContainer container mx-auto p-4">
        <h1 class="text-2xl font-bold text-white">Your Documents</h1>
        <p class="text-lg text-gray-300">Here are the documents you have created or have been shared with you.</p>
        <h2 class="text-xl font-semibold text-white">Created by you</h2>
        

        <div class="documentGrid">
            <DocumentCard document={{ title: "Document 1 aaaaaaa a a adasd asd as d", lastEdit: "Last edited 2 days ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
            <DocumentCard document={{ title: "Document 2", lastEdit: "Last edited 5 days ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
            <DocumentCard document={{ title: "Document 3", lastEdit: "Last edited 1 week ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
        </div>

        <h2 class="text-xl font-semibold text-white">Shared with you</h2>
        <div class="documentGrid">
            <DocumentCard document={{ title: "Document 4", lastEdit: "Last edited 3 days ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
            <DocumentCard document={{ title: "Document 5", lastEdit: "Last edited 1 week ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
            <DocumentCard document={{ title: "Document 6", lastEdit: "Last edited 2 weeks ago", preview: "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e" }} />
        </div>
	</div>
</div>

<style>
    .documentGrid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: 1rem;
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