<script lang="ts">
	import { Annotation, EditorState, Transaction } from '@codemirror/state';
	import { EditorView, keymap, lineNumbers } from '@codemirror/view';
	import { defaultKeymap } from '@codemirror/commands';
	import { onDestroy, onMount } from 'svelte';
	import { DeleteOperation, InsertOperation, type Operation } from '$lib/operations';

	let { class: className = '', documentState = $bindable(), onchange = (operations: Operation[]) => {} } = $props();
	let editorContainer: HTMLDivElement;

	let view: EditorView;
	const SkipMonitorAnnotation = Annotation.define();

	onMount(() => {
		const handleChanges = (tr: Transaction) => {
			if (!tr.docChanged) return; // Skip if there are no changes

			let oldDoc = documentState.content as string;
			let currentRevision = documentState.revision;
			console.log('Current revision:', currentRevision);

			// update the document state
			documentState.content = tr.newDoc.toString();

			// Skip monitoring changes if the annotation is present
			if (tr.annotation(SkipMonitorAnnotation)) return;

			let changes: Operation[] = [];

			tr.changes.iterChanges(
				(
					fromA: number,
					toA: number,
					fromB: any,
					toB: any,
					inserted: { length: number; sliceString: (arg0: number, arg1: any) => any }
				) => {
					// Handle deletion
					if (toA > fromA) {
						const deletionIndex = fromA; // Start index of the deletion
						const deletionLength = toA - fromA; // Length of the deleted text
						changes.push(new DeleteOperation(currentRevision, deletionIndex, deletionLength));
					}

					// Handle insertion
					if (inserted.length > 0) {
						const insertionIndex = fromB; // Start index of the insertion
						const insertedText = inserted.sliceString(0, inserted.length); // Text inserted
						changes.push(new InsertOperation(currentRevision, insertionIndex, insertedText));
					}

				}
			);

			let combinedChanges: Operation[] = [];

			if (changes.length == 2) {
				let first = changes[0];
				let second = changes[1];

				if (first instanceof InsertOperation && second instanceof DeleteOperation) {
					if (first.index == second.index && first.content.length == second.length) {
						changes = [];
					}
				} else if (first instanceof DeleteOperation && second instanceof InsertOperation) {
					if (first.index === second.index) {
						const deletedText = oldDoc.slice(first.index, first.index + first.length);
						const insertedText = second.content;

						if (deletedText === insertedText) {
							// The deleted text is the same as the inserted text; skip both
							changes = [];
						} else if (insertedText.startsWith(deletedText)) {
							// Inserted text extends beyond the deleted text; record the additional insertion
							const extraText = insertedText.slice(deletedText.length);

							new InsertOperation(first.revision, first.index + first.length, extraText);
						} else {
							// Record as separate delete and insert if they do not overlap meaningfully
							combinedChanges.push(first);
							combinedChanges.push(second);
						}
					} else {
						// Non-overlapping delete and insert
						combinedChanges.push(first);
						combinedChanges.push(second);
					}
				}
			}

			if (combinedChanges.length > 0) changes = combinedChanges;

			if (changes.length > 0) {
				console.log('Changes:', changes);
				onchange(changes);
			}
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

	// Function to insert text at a specific index
	export function insertText(index: number, text: string) {
		const transaction = view.state.update({
			changes: { from: index, insert: text }, // Specify insertion
			annotations: SkipMonitorAnnotation.of(true), // Add annotation
			scrollIntoView: false
		});

		view.dispatch(transaction);
	}

	// Function to delete a range of text
	export function deleteText(index: number, len: number) {
		const transaction = view.state.update({
			changes: { from: index, to: index + len }, // Specify deletion
			annotations: SkipMonitorAnnotation.of(true), // Add annotation
			scrollIntoView: false
		});

		view.dispatch(transaction);
	}
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
