package com.example.jegarcia.VolunteerMaps.ui.instagram;

public class InstagramData {

    private Images images;
    private User user;

    public Images getImages() {
        return images;
    }

    public User getUser() {
        return user;
    }

    public class User {

        private String profile_picture;

        private String full_name;

        public String getProfile_picture() {
            return profile_picture;
        }

        public String getFull_name() {
            return full_name;
        }
    }

    public class Images {

        private Standard_resolution standard_resolution;
        private Low_resoluton low_resolution;

        public Standard_resolution getStandard_resolution() {
            return standard_resolution;
        }

        public Low_resoluton getLow_resolution() {
            return low_resolution;
        }

        public class Standard_resolution {

            private String url;

            public String getUrl() {
                return url;
            }
        }

        public class Low_resoluton {
            private String url;

            public String getUrl() {
                return url;
            }

        }
    }
}