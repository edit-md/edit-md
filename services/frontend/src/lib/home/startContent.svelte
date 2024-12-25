<script>
	import IconGithub from "$lib/icons/iconGithub.svelte";

	async function login() {
		// clear the EDITMD_SESSION cookie
		
	}

</script>
<div class="w-full bg-background">
	<section class="container mx-auto flex h-screen items-center">
		<div>
			<h1 class="text-6xl"># edit.md</h1>
			<h2 class="mt-2 text-4xl text-foreground-80">The collaborative Markdown editor</h2>

			<a href="/api/accounts/oauth2/authorization/github">
				<button class="text-whitetext-xl mt-6 rounded-md bg-muted px-4 py-4 flex items-center gap-4 hover:bg-foreground-10">
					<IconGithub class="w-6 h-6" />
					Sign in with GitHub
				</button>
			</a>
		</div>
	</section>
</div>
