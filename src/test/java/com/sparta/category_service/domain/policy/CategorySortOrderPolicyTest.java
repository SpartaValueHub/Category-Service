package com.sparta.category_service.domain.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class CategorySortOrderPolicyTest {

	@Test
	void emptySiblings_appendFirst() {
		var result = CategorySortOrderPolicy.place(List.of(), 0);

		assertEquals(1000, result.insertedSortOrder());
		assertTrue(result.siblingUpdates().isEmpty());
	}

	@Test
	void appendAfterLast() {
		var result = CategorySortOrderPolicy.place(List.of(1000, 2000, 4000), 3);

		assertEquals(5000, result.insertedSortOrder());
		assertTrue(result.siblingUpdates().isEmpty());
	}

	@Test
	void insertBetweenGap() {
		var result = CategorySortOrderPolicy.place(List.of(1000, 2000, 4000, 5000, 6000), 3);

		assertEquals(4500, result.insertedSortOrder());
		assertTrue(result.siblingUpdates().isEmpty());
	}

	@Test
	void insertBeforeFirst() {
		var result = CategorySortOrderPolicy.place(List.of(1000, 2000), 0);

		assertEquals(500, result.insertedSortOrder());
		assertTrue(result.siblingUpdates().isEmpty());
	}

	@Test
	void gapExhausted_rebalancesSiblings() {
		var result = CategorySortOrderPolicy.place(List.of(1000, 1001), 1);

		assertEquals(2000, result.insertedSortOrder());
		assertEquals(1, result.siblingUpdates().size());
		assertEquals(1, result.siblingUpdates().get(0).siblingIndex());
		assertEquals(3000, result.siblingUpdates().get(0).newSortOrder());
	}
}
