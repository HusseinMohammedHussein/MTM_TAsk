package com.e.mtmtask.Models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Hussein on 04/02/2021
 */
@Setter
@Getter
public class DestinationLocationPojo {
    private List<CandidatePojo> candidates;

    @Getter
    @Setter
    public class CandidatePojo {
        private GeometryPojo geometry;
        private String name;

        @Getter
        @Setter
        public class GeometryPojo {
            private LocationPojo location;

            @Getter
            @Setter
            public class LocationPojo {
                private double lat;
                private double lng;
            }
        }
    }
}
