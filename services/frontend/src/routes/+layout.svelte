<script lang="ts">
	import { onMount } from 'svelte';
	import '../app.css';
	let { children } = $props();
</script>

{@render children()}
