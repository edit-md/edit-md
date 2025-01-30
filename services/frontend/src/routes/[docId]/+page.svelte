<script lang="ts">
	import Header from '$lib/components/header.svelte';
	import type { PageData } from './$types';
	import { page } from '$app/state';
	import MarkdownViewer from '$lib/components/markdownViewer.svelte';
	import { goto } from '$app/navigation';

	import Editor from '$lib/components/editor.svelte';
	import { browser } from '$app/environment';
	import { onDestroy, onMount } from 'svelte';
	import LiveConnection from '$lib/liveConnection';
	import { Operation, InsertOperation, DeleteOperation, transform } from '$lib/operations';
	import OperationQueue from '$lib/operationQueue';
	import IconPreview from '$lib/icons/iconPreview.svelte';
	import { setCookie } from '$lib/cookieUtils';
	import { finishFileUpload, initFileUpload, openFileUpload, uploadFile } from '$lib/fileUploadLib';
	import IconPen from '$lib/icons/iconPen.svelte';
	import IconEye from '$lib/icons/iconEye.svelte';
	import IconInsert from '$lib/icons/iconInsert.svelte';

	let { data }: { data: PageData } = $props();

	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	let documentState = $state({
		id: data.document?.id || '',
		title: data.document?.title || 'Untitled',
		content: data.document?.content || '',
		revision: data.document?.revision || 0,
		lastCursor: 0
	});

	let isOwner = data.document?.owner == user?.id;
	let isShared = data.document?.shared?.find((shared) => shared.id === user?.id && shared.permission == "WRITE") !== undefined;

	let viewState = $state({
		view: null,
		onlyPreview: !isOwner && !isShared,
		previewActive: data.previewActive === true,
		previewResizing: false
	});

	function togglePreview() {
		viewState.previewActive = !viewState.previewActive;
		setCookie('previewActive', viewState.previewActive ? 'true' : 'false');
	}

	let editor: Editor;
	let socket: LiveConnection;
	let operationQueue: OperationQueue = new OperationQueue();

	if (browser) {
		socket = new LiveConnection(
			`wss://${window.location.hostname}/api/documents/${data.document?.id}/live`
		);
		socket.onMessage((message) => {
			let json = JSON.parse(message.data);

			let ack = message.commandId === 1;
			// set the revision to the latest revision
			if (ack) {
				operationQueue.map((op) => {
					op.revision = json.r;
					return op;
				});
				documentState.revision = json.r;
				socket.ack();
				return;
			}

			let remoteOp: Operation;

			if (json.t === 0) {
				// Insert
				remoteOp = new InsertOperation(json.r, json.i, json.c);
			} else if (json.t === 1) {
				// Delete
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
			if (remoteOp instanceof InsertOperation) {
				if(editor) {
					editor.insertText(remoteOp.index, remoteOp.content);
				} else {
					documentState.content = documentState.content.slice(0, remoteOp.index) + remoteOp.content + documentState.content.slice(remoteOp.index);
				}
			} else if (remoteOp instanceof DeleteOperation) {
				if(editor) {
					editor.deleteText(remoteOp.index, remoteOp.length);
				} else {
					documentState.content = documentState.content.slice(0, remoteOp.index) + documentState.content.slice(remoteOp.index + remoteOp.length);
				}
			}

			// transform operations in the operation queue
			operationQueue.map((localOp) => {
				return transform(localOp, remoteOp);
			});

			documentState.revision = json.r;
		});
	}

	let contentContainer: HTMLDivElement;

	onMount(() => {
		document.addEventListener('mousemove', (e) => {
			if (!viewState.previewResizing) return;

			const x = e.pageX;
			const rect = contentContainer.getBoundingClientRect();
			const width = x - rect.left;

			if (width < 300) return;

			if (width > rect.width - 300) return;

			contentContainer.style.setProperty('--editor-width', `${width}px`);
		});

		document.addEventListener('mouseup', (e) => {
			if (!viewState.previewResizing) return;

			viewState.previewResizing = false;
			document.body.style.cursor = 'auto';
			document.body.style.userSelect = 'auto';
		});
	})

	onDestroy(() => {
		if (socket) {
			socket.close();
		}
	});
</script>

<div class="pageContainer bg-background">
	<Header {user} fullWidth={!viewState.onlyPreview}>
		{#snippet center()}
			<h1 class="pageTitle">{documentState.title}</h1>
		{/snippet}
		{#snippet right()}
			{#if viewState.onlyPreview == false}
				<button 
				class="transition-all duration-150 hover:bg-foreground-10 rounded-md p-1"
				aria-label="Upload file"
				onclick={
					async () => {
						const file = await openFileUpload();
						const fileInfo = await initFileUpload(data.document?.id, file);
						await uploadFile(fileInfo, file);
						await finishFileUpload(fileInfo);

						if(file.type.startsWith('image/')) {
							editor.insertText(documentState.lastCursor, `![${file.name}](https://${window.location.hostname}/api/files/image/${fileInfo.id})`, true);
						} else {
							editor.insertText(documentState.lastCursor, `![${file.name}](https://${window.location.hostname}/api/files/${fileInfo.id})`, true);
						}
					}
				}>
					<IconInsert class="h-5 w-5" />
				</button>
			
				<button
					class="transition-all duration-150 hover:bg-foreground-10 rounded-md p-1"
					aria-label="Toggle preview"
					onclick={togglePreview}
				>
					<IconPreview class="hidden h-5 w-5 md:block" />
					{#if viewState.previewActive}
						<IconPen class="h-5 w-5 md:hidden" />
					{:else}
						<IconEye class="h-5 w-5 md:hidden" />
					{/if}
				</button>
			{/if}
		{/snippet}
	</Header>
	<div class="contentContainer" bind:this={contentContainer}>
		{#if viewState.onlyPreview}
			<MarkdownViewer content={documentState.content} files={data.files} inContainer={true} />
		{:else}
			<div class="editorContainer h-full overflow-auto {viewState.previewActive ? 'hidden md:block' : 'col-span-3'}">
				<Editor
					class="h-full"
					bind:documentState
					onchange={(ops: Operation[]) => {
						if (socket) {
							for (let op of ops) {
								operationQueue.push(op);
								socket.queueFromQueue(operationQueue);
							}
						}
					}}
					bind:this={editor}
				></Editor>
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
				<MarkdownViewer content={documentState.content} files={data.files} class="col-span-3 md:col-span-1" />
			{/if}
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
		grid-template-columns: 1fr;
		height: 100%;
		max-height: 100%;
		overflow: hidden; /* Ensures overflow behavior is managed */
		--editor-width: 1fr;
	}

	.contentContainer:has(> #resizer) {
		grid-template-columns: var(--editor-width) min-content 1fr;
	}

	#editorGrid {
		height: 100%;
		overflow: auto; /* Enables scrolling within the editor grid */
	}

	.pageTitle {
		@apply truncate;
	}

	#resizer {
		@apply bg-background-alt cursor-ew-resize;
		width: 3px;
	}
</style>
