<script lang="ts">
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	async function logout() {
		await fetch('https://127.0.0.1/api/accounts/logout', {
			method: 'POST',
			headers: {
				'X-CSRF-Protection': '1'
			}
		});

		loggedIn = false;

		return false;
	}
</script>

<h1>Welcome to the frontend</h1>

{#if loggedIn}
	<p>Welcome, {user.name}</p>
	<img src={user.avatar} alt="User avatar" />
	<button onclick={logout}>Logout</button>
{:else}
	<p>You are not logged in</p>
	<a href="https://127.0.0.1/api/accounts/oauth2/authorization/github">
		<button>Login with GitHub</button>
	</a>
{/if}
