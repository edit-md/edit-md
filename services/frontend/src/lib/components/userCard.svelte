<script>
	import { onMount } from "svelte";

    let { userId } = $props();

    let user;

    onMount(async () => {
        let req = await fetch(`/api/accounts/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Protection': '1'
            }
        });

        if (req.ok) {
            user = await req.json();
        }
    })
</script>

<div class="flex items-center space-x-2">
    <img src={user.avatar} alt={user.name} class="h-6 w-6 rounded-full" />
    <p class="text-sm">{user.name}</p>
</div>