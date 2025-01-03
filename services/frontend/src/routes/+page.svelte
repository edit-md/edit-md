<script lang="ts">
	import DashboardContent from '$lib/home/dashboardContent.svelte';
	import StartContent from '$lib/home/startContent.svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);
</script>

{#if loggedIn}
	<DashboardContent {user} documents={data.documents} />
{:else}
	<StartContent />
{/if}
