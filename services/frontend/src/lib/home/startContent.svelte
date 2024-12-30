<script>
	import IconGithub from '$lib/icons/iconGithub.svelte';

	async function login() {
		window.location.href = '/api/accounts/oauth2/authorization/github';
	}
</script>

<div class="w-full bg-background">
	<section class="container mx-auto flex h-screen items-center p-4">
		<div>
			<h1 class="text-6xl"># edit.md</h1>
			<h2 class="mt-2 text-4xl text-foreground-80">The collaborative Markdown editor</h2>

			<button
				class="text-whitetext-xl mt-6 flex items-center gap-4 rounded-md bg-foreground-10 px-4 py-4 hover:bg-foreground-20"
				onclick={login}
			>
				<IconGithub class="h-6 w-6" />
				Sign in with GitHub
			</button>
		</div>
	</section>
</div>
