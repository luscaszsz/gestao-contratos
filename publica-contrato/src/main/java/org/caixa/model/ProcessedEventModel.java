package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "processed_event")
public class ProcessedEventModel extends PanacheEntityBase {
    @Id
    @Column(name = "event_id", nullable = false, length = 64)
    public String eventId;

    @Column(name = "event_type", nullable = false, length = 64)
    public String eventType;

    @Column(name = "processed_ad", nullable = false, length = 64)
    public OffsetDateTime processedAt;
}
