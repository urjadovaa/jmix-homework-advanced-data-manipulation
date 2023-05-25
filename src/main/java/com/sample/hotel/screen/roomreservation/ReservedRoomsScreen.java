package com.sample.hotel.screen.roomreservation;

import com.sample.hotel.entity.Client;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import io.jmix.core.Id;
import io.jmix.ui.Dialogs;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("ReservedRooms")
@UiDescriptor("reserved-rooms.xml")
@LookupComponent("roomReservationsTable")
public class ReservedRoomsScreen extends StandardLookup<RoomReservation> {
    @Autowired
    private GroupTable<RoomReservation> roomReservationsTable;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FetchPlans fetchPlans;

    @Subscribe("roomReservationsTable.viewClientEmail")
    public void onRoomReservationsTableViewClientEmail(Action.ActionPerformedEvent event) {

        RoomReservation reservation = roomReservationsTable.getSingleSelected();
        if (reservation == null) return;

        // перезагрузка сущности Client с нужным атрибутом
        FetchPlan clientEmailFetchPlan = fetchPlans.builder(RoomReservation.class)
                .add("id")
                .add("booking.client.email")
                .build();
        reservation = dataManager.load(Id.of(reservation)).fetchPlan(clientEmailFetchPlan).one();

        Client client = reservation.getBooking().getClient();

        dialogs.createMessageDialog()
                .withCaption("Client email")
                .withMessage(client.getEmail())
                .show();
    }
}