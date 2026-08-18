package com.sparta.category_service.domain.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

// 같은 부모 형제 간 sort_order gap 배치 정책 (DB 정렬 가중치)
public final class CategorySortOrderPolicy {

	// 형제 간 기본 간격
	public static final int GAP_STEP = 1000;
	// 사이 값을 넣을 수 있는 최소 여유
	public static final int MIN_GAP = 1;
	// 첫 항목 기본값
	public static final int INITIAL = GAP_STEP;

	private CategorySortOrderPolicy() {
	}

	// 활성 형제 sort_order 목록과 삽입 인덱스로 배치 결과 계산
	public static PlacementResult place(List<Integer> siblingSortOrders, int insertIndex) {
		if (insertIndex < 0 || insertIndex > siblingSortOrders.size()) {
			throw new IllegalArgumentException("삽입 위치가 형제 범위를 벗어났습니다.");
		}

		Integer prev = insertIndex > 0 ? siblingSortOrders.get(insertIndex - 1) : null;
		Integer next = insertIndex < siblingSortOrders.size() ? siblingSortOrders.get(insertIndex) : null;

		OptionalInt gapValue = tryBetween(prev, next);
		if (gapValue.isPresent()) {
			return new PlacementResult(gapValue.getAsInt(), List.of());
		}

		return rebalancePlacement(siblingSortOrders, insertIndex);
	}

	// prev·next 사이 gap 값 (없으면 empty → rebalance)
	public static OptionalInt tryBetween(Integer prev, Integer next) {
		if (prev == null && next == null) {
			return OptionalInt.of(INITIAL);
		}
		if (prev == null) {
			if (next > MIN_GAP) {
				return OptionalInt.of(next / 2);
			}
			return OptionalInt.empty();
		}
		if (next == null) {
			return OptionalInt.of(prev + GAP_STEP);
		}
		if (next - prev > MIN_GAP) {
			return OptionalInt.of(prev + (next - prev) / 2);
		}
		return OptionalInt.empty();
	}

	// n개 항목에 1000, 2000, … gap 재부여
	public static List<Integer> rebalance(int count) {
		List<Integer> values = new ArrayList<>(count);
		for (int index = 0; index < count; index++) {
			values.add((index + 1) * GAP_STEP);
		}
		return values;
	}

	private static PlacementResult rebalancePlacement(List<Integer> siblingSortOrders, int insertIndex) {
		int totalCount = siblingSortOrders.size() + 1;
		List<Integer> rebalanced = rebalance(totalCount);
		int insertedSortOrder = rebalanced.get(insertIndex);

		List<SiblingSortOrderUpdate> siblingUpdates = new ArrayList<>();
		for (int siblingIndex = 0; siblingIndex < siblingSortOrders.size(); siblingIndex++) {
			int rebalancedIndex = siblingIndex < insertIndex ? siblingIndex : siblingIndex + 1;
			int newSortOrder = rebalanced.get(rebalancedIndex);
			if (newSortOrder != siblingSortOrders.get(siblingIndex)) {
				siblingUpdates.add(new SiblingSortOrderUpdate(siblingIndex, newSortOrder));
			}
		}

		return new PlacementResult(insertedSortOrder, siblingUpdates);
	}

	// 배치 결과 (신규/이동 대상 sort_order + rebalance로 바뀌는 형제)
	public record PlacementResult(
			int insertedSortOrder,
			List<SiblingSortOrderUpdate> siblingUpdates
	) {
	}

	// rebalance 시 형제 sort_order 변경 (siblingSortOrders 리스트 인덱스 기준)
	public record SiblingSortOrderUpdate(
			int siblingIndex,
			int newSortOrder
	) {
	}
}
