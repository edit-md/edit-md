<script lang="ts">
	import Header from '$lib/components/header.svelte';
	import type { PageData } from './$types';
	import { page } from '$app/state';
	import MarkdownViewer from '$lib/components/markdownViewer.svelte';
	import { goto } from '$app/navigation';

	import Editor from '$lib/components/editor.svelte';
	import { browser } from '$app/environment';
	import { onDestroy } from 'svelte';
	import LiveConnection from '$lib/liveConnection';
	import { Operation, InsertOperation, DeleteOperation, transform } from '$lib/operations';
	import OperationQueue from '$lib/operationQueue';

	let { data }: { data: PageData } = $props();

	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	let documentState = $state({
		title: data.document?.title || 'Untitled',
		content: data.document?.content || '',
		revision: data.document?.revision || 0
	});

	let viewState = $state({
		view: null,
		previewActive: true,
		previewResizing: false
	});

	function viewDocument() {
		goto(page.url.href + '/view');
	}

	let editor : Editor;
	let socket : LiveConnection;
	let operationQueue: OperationQueue = new OperationQueue();

	if(browser) {
		socket = new LiveConnection(`wss://${window.location.hostname}/api/documents/${data.document?.id}/live`);
		socket.onMessage((message) => {
			
			console.log(message);
			let json = JSON.parse(message.data);
			
			let ack = message.commandId === 1;
			// set the revision to the latest revision
			if (ack) {
				operationQueue.map(op => {
					console.log('Updating revision:', op, json.r);
					op.revision = json.r;
					console.log('Updated revision:', op);
					return op;
				});
				documentState.revision = json.r;
				socket.ack();
				return;
			}

			let remoteOp: Operation;

			if(json.t === 0) { // Insert
				remoteOp = new InsertOperation(json.r, json.i, json.c);
			} else if(json.t === 1) { // Delete
				remoteOp = new DeleteOperation(json.r, json.i, json.l);
			} else {
				console.error('Unknown operation type:', json.type);
				return;
			}

			socket.getWaitingForAck().forEach((localOp) => {
				const transformedOp = transform(remoteOp, localOp.obj);
				if (transformedOp) {
					remoteOp = transformedOp;
				} else {
					return;
				}
			});

			operationQueue.forEach((localOp) => {
				const transformedOp = transform(remoteOp, localOp);
				if (transformedOp) {
					remoteOp = transformedOp;
				} else {
					return;
				}
			});

			// apply remote oberations
			if(remoteOp instanceof InsertOperation) {
				editor.insertText(remoteOp.index, remoteOp.content);
			} else if(remoteOp instanceof DeleteOperation) {
				editor.deleteText(remoteOp.index, remoteOp.length);
			}

			// transform operations in the operation queue
			operationQueue.map((localOp) => {
				return transform(localOp, remoteOp);
			});

			documentState.revision = json.r;
		});
	}

	onDestroy(() => {
		if(socket) {
			socket.close();
		}
	});

</script>

<div class="pageContainer bg-background">
	<Header {user} fullWidth={true}>
		{#snippet center()}
			<h1 class="pageTitle">{documentState.title}</h1>
		{/snippet}
		{#snippet right()}
			<button class="rounded-md bg-foreground-10 px-4 py-1" onclick={viewDocument}>View</button>
		{/snippet}
	</Header>
	<div class="contentContainer">
		<div
			class="editorContainer h-full overflow-auto {viewState.previewActive
				? 'hidden md:block'
				: 'col-span-3'}"
		>
			<Editor class="col-span-3 h-full" bind:documentState onchange={(ops: Operation[]) => {
				if(socket) {
					for(let op of ops) {
						operationQueue.push(op);
						socket.queueFromQueue(operationQueue);
					}
				}
			}} bind:this={editor}></Editor>
		</div>
		{#if viewState.previewActive}
			<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
			<div
				id="resizer"
				role="separator"
				onmousedown={() => {
					viewState.previewResizing = true;
					document.body.style.cursor = 'ew-resize';
					// disable text selection
					document.body.style.userSelect = 'none';
				}}
				class="hidden md:block"
			></div>
			<MarkdownViewer content={documentState.content} class="col-span-3 md:col-span-1" />
		{/if}
	</div>
</div>

<style>
	.pageContainer {
		width: 100%;
		height: 100dvh;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.contentContainer {
		background-color: var(--color-background);
		display: grid;
		grid-template-columns: 1fr min-content 1fr;
		height: 100%;
		max-height: 100%;
		overflow: hidden; /* Ensures overflow behavior is managed */
	}

	#editorGrid {
		height: 100%;
		overflow: auto; /* Enables scrolling within the editor grid */
	}

	.pageTitle {
		@apply truncate;
	}

	#resizer {
		@apply cursor-ew-resize bg-background-alt;
		width: 3px;
	}
</style>
