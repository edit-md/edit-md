<script lang="ts">
	import { goto } from '$app/navigation';
	import { page } from '$app/state';
	import { optionalChildren } from '$lib/svelteUtils';
	import Profile from './profile.svelte';

	interface Props {
		user?: any;
		fullWidth?: boolean;
		left?: any;
		center?: any;
		right?: any;
	}

	let {
		user,
		fullWidth = false,
		left = optionalChildren,
		center = optionalChildren,
		right = optionalChildren
	}: Props = $props();

	function home() {
		if (page.url.pathname !== '/') {
			goto('/');
		}
	}
</script>

<div class="headerContainer">
	<div class="headerContent {fullWidth ? 'fullWidth' : ''}">
		<div class="left">
			<button class="pageTitle" onclick={home}># edit.md</button>
			{@render left()}
		</div>
		<div class="center">
			{@render center()}
		</div>
		<div class="right">
			{@render right()}
			{#if user}
				<Profile {user} style="view-transition-name: headerProfile;" />
			{/if}
		</div>
	</div>
</div>

<style>
	.headerContainer {
		@apply sticky top-0 z-[100] h-fit bg-background-alt;
		view-transition-name: headerContainer;
	}

	.headerContent {
		@apply grid h-fit items-center justify-between gap-4 p-4;
		grid-template-columns: minmax(auto, 1fr) auto minmax(auto, 1fr);
	}

	.headerContent:not(.fullWidth) {
		@apply container mx-auto;
	}

	.left {
		@apply flex items-center justify-start gap-2;
		view-transition-name: headerLeft;
	}

	.center {
		@apply flex items-center justify-center gap-2;
		view-transition-name: headerCenter;
		flex-shrink: 1;
		min-width: 0; /* Prevent overflow */
	}

	.right {
		@apply flex items-center justify-end gap-2;
		view-transition-name: headerRight;
	}

	.pageTitle {
		@apply cursor-pointer select-none text-nowrap text-xl;
		view-transition-name: headerPageTitle;
	}
</style>
