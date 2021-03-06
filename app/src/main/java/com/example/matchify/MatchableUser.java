package com.example.matchify;

import java.util.List;

/**
 * Created by deb on 24/9/16.
 */
public class MatchableUser {
    private String name;
    private List<String> topArtists;
    private List<String> topTracks;
    private String email;
    private String phoneNumber;

    public MatchableUser(String name, String email, String phoneNumber, List<String> topArtists, List<String> topTracks) {
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setTopArtists(topArtists);
        setTopTracks(topTracks);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<String> getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(List<String> topArtists) {
        while (topArtists.size() > 3) {
            topArtists.remove(topArtists.size() - 1);
        }
        if (topArtists.size() == 0) {
            topArtists.add("No known top artists");
        }
        this.topArtists = topArtists;
    }

    public List<String> getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(List<String> topTracks) {
        while (topTracks.size() > 3) {
            topTracks.remove(topTracks.size() - 1);
        }
        if (topTracks.size() == 0) {
            topTracks.add("No known top tracks");
        }
        this.topTracks = topTracks;
    }

    @Override
    public String toString() {
        if (name.equals("name")) {
            return "Swipe to start matching";
        }

        if (name.equals("end")) {
            return "Swipe to continue";
        }

        StringBuilder result = new StringBuilder(name);
        result.append("\n\n\n" + "Favorite Artists: " + topArtists.get(0));
        for (int i = 1; i < topArtists.size(); i++) {
            result.append(", " + topArtists.get(i));
        }

        result.append("\n\n\n" + "Favorite Tracks: " + topTracks.get(0));
        for (int i = 1; i < topTracks.size(); i++) {
            result.append(", " + topTracks.get(i));
        }

        return result.toString();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
