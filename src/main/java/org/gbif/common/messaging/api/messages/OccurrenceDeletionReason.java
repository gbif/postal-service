package org.gbif.common.messaging.api.messages;

/**
 * The reason an occurrence was deleted - used for change tracking afterwards.
 */
public enum OccurrenceDeletionReason {
  OCCURRENCE_MANUAL, DATASET_MANUAL, NOT_SEEN_IN_LAST_CRAWL
}
