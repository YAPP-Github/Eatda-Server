package eatda.facade;

import eatda.domain.cheer.Cheer;
import eatda.domain.store.Store;

public record CheerCreationResult(Cheer cheer, Store store) {
}
