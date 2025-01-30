<script lang="ts">
	import Header from '$lib/components/header.svelte';
	import { fetchConnectedAccounts } from '$lib/connectedAccountLib';
	import IconGithub from '$lib/icons/iconGithub.svelte';
	import { onMount } from 'svelte';
	import type { PageData } from './$types';
	import { dev } from '$app/environment';
	import DebugArea from '$lib/components/debugArea.svelte';

	let { data }: { data: PageData } = $props();
	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	onMount(async () => {
		user.connectedAccounts = await fetchConnectedAccounts(user.connectedAccounts);
	});
</script>

<svelte:head>
	<title>edit.md - Account</title>
</svelte:head>

<div class="pageContainer bg-background">
	<Header {user}></Header>
	<div class="contentContainer container mx-auto p-4">
		<h1 class="mb-2 text-2xl font-bold">Your Account</h1>

		<div class="flex flex-col gap-2">
			<div class="flex gap-4">
				<div
					class="aspect-square h-32 rounded-md bg-contain"
					style="background-image: url({user.avatar});"
				></div>
				<div>
					<h2 class="text-xl font-bold">{user.name}</h2>
					<p class="text-sm text-foreground-60">{user.email}</p>

					<div class="mt-2 flex gap-2">
						{#each user.connectedAccounts as acc}
							{#if acc.provider === 'github'}
								<a href={acc.user?.html_url ?? ''} target="_blank">
									<IconGithub class="h-6 w-6 text-foreground"></IconGithub>
								</a>
							{/if}
						{/each}
					</div>
				</div>
			</div>
			{#if dev}
				<div class="rounded-md bg-foreground-10 p-4">
					<h2 class="text-xl font-bold">Development</h2>
					<p class="mb-2 text-sm text-foreground-80">
						The information below is only visible during development mode. This can be useful for
						debugging purposes and understanding the current state of the application.
					</p>

					<h3 class="text-lg">User Info:</h3>
					<DebugArea value={user} class="mb-2 text-sm"></DebugArea>

					<h3 class="text-lg">API Request Headers:</h3>
					<DebugArea
						value={`Cookie: EDITMD_SESSION=${data.sessionCookie}\nX-CSRF-PROTECTION: 1`}
						class="text-sm"
					></DebugArea>
				</div>
			{/if}
		</div>
	</div>
</div>
