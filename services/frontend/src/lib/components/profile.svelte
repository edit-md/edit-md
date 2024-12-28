<script lang="ts">
	import { DropdownMenu } from 'bits-ui';

	interface Props {
		user: {
			avatar: string;
		};
		class?: string;
		style?: string;
	}

	let { user, class: className = '', style = '' }: Props = $props();

	async function logout() {
		await fetch('/api/accounts/logout', {
			method: 'POST'
		});

		window.location.href = '/';
	}
</script>

<DropdownMenu.Root>
	<DropdownMenu.Trigger
		class="h-8 w-8 rounded-full bg-background-alt bg-contain hover:opacity-70 {className}"
		style="background-image: url({user.avatar}); {style}"
	></DropdownMenu.Trigger>
	<DropdownMenu.Content
		class="z-[1000] w-full max-w-[229px] rounded-lg border border-foreground-20 bg-background px-1 py-[5px] shadow-popover"
		sideOffset={8}
		align="end"
	>
		<DropdownMenu.Item
			class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-foreground-20"
		>
			<div class="flex items-center">Profile</div>
		</DropdownMenu.Item>
		<DropdownMenu.Item
			class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-foreground-20"
			onclick={logout}
		>
			<div class="flex items-center">Logout</div>
		</DropdownMenu.Item>
	</DropdownMenu.Content>
</DropdownMenu.Root>
