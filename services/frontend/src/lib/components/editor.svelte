<script lang="ts">
	import { Annotation, EditorState, Transaction } from '@codemirror/state';
	import { EditorView, keymap, lineNumbers } from '@codemirror/view';
	import { defaultKeymap } from '@codemirror/commands';
	import { onDestroy, onMount } from 'svelte';

	let { class: className = '', documentState = $bindable() } = $props();
	let editorContainer: HTMLDivElement;

	let view: EditorView;

	onMount(async () => {
		const handleChanges = (tr: Transaction) => {
			if (!tr.docChanged) return; // Skip if there are no changes

			let oldDoc = documentState.content as string;

			// update the document state
			documentState.content = tr.newDoc.toString();
		};

		const changeMonitor = EditorView.updateListener.of((update) => {
			if (update.transactions) {
				for (let tr of update.transactions) {
					handleChanges(tr);
				}
			}
		});

		let myTheme = EditorView.theme({
			'&': {
				color: 'hsl(var(--foreground))',
				backgroundColor: 'hsl(var(--background))'
			},
			'.cm-content': {
				caretColor: 'hsl(var(--foreground)/0.75)'
			},
			'.cm-lineNumbers': {
				color: 'hsl(var(--foreground-alt))',
				'min-width': '5ch'
			},
			'&.cm-focused .cm-cursor': {
				borderLeftColor: 'hsl(var(--foreground)/0.3)'
			},
			'&.cm-focused .cm-selectionBackground, ::selection': {
				backgroundColor: 'hsl(var(--foreground)/0.3)'
			},
			'.cm-gutters': {
				backgroundColor: 'hsl(var(--background-alt))',
				color: 'hsl(var(--foreground))',
				border: 'none'
			}
		});

		let startState = EditorState.create({
			doc: documentState.content,
			extensions: [
				keymap.of(defaultKeymap),
				EditorView.lineWrapping,
				changeMonitor,
				myTheme,
				lineNumbers()
			]
		});

		view = new EditorView({
			state: startState,
			parent: editorContainer
		});
	});

	onDestroy(() => {
		if (view) view.destroy();
	});
</script>

<div bind:this={editorContainer} class={className}></div>

<style>
	:global(.cm-editor .cm-scroller) {
		font-family: 'JetBrains Mono', monospace !important;
	}

	:global(.cm-editor) {
		@apply h-full;
	}
</style>
