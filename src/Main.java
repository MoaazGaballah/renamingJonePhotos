import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static class newPhoto {
        String city;
        String number;
        String extension;
        int fileKey;

        public newPhoto(String city, String number, String extension, int fileKey) {
            this.city = city;
            this.number = number;
            this.extension = extension;
            this.fileKey = fileKey;
        }
    }

    public static class OldPhoto {
        String date;
        String time;
        String city;
        String extension;
        int fileKey;

        public OldPhoto(String date, String time, String city, String extension, int fileKey) {
            this.date = date;
            this.time = time;
            this.city = city;
            this.extension = extension;
            this.fileKey = fileKey;
        }
    }

    static ArrayList<String> listOfLines = new ArrayList<>();

    public static ArrayList<String> readingStringLineByLine(String s) throws IOException {

        ArrayList<OldPhoto> oldPhotos = readFile(s);

        // sort by city
        sortByField(oldPhotos, " ");

        // group cities together
        ArrayList<ArrayList<OldPhoto>> oldPhotosSplittedByCities = splitByCities(oldPhotos);

        // sort by date and time
        ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotosSplittedAndSortedByDateAndTime = splitByDateAndTime(oldPhotosSplittedByCities);

        // get a list for new photos
        getNewPhotos(oldPhotosSplittedAndSortedByDateAndTime);

        // return the result
        return listOfLines;
    }

    public static ArrayList<ArrayList<ArrayList<newPhoto>>> getNewPhotos(ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotos) {
        ArrayList<ArrayList<ArrayList<newPhoto>>> newPhotos = new ArrayList<>();
        for (int i = 0; i < oldPhotos.size(); i++) {
            ArrayList<ArrayList<newPhoto>> nestedPhotos = new ArrayList<>();
            for (int j = 0; j < oldPhotos.get(i).size(); j++) {
                ArrayList<newPhoto> photos = new ArrayList<>();
                for (int k = 0; k < oldPhotos.get(i).get(j).size(); k++) {
                    int length = String.valueOf(getMax(oldPhotos.get(i).get(j))).length();
                    String number = getNumber(oldPhotos.get(i).get(j).get(k).fileKey, length);
                    String city = oldPhotos.get(i).get(j).get(k).city;
                    String extension = oldPhotos.get(i).get(j).get(k).extension;
                    int fileKey = oldPhotos.get(i).get(j).get(k).fileKey;
                    newPhoto np = new newPhoto(city, number, extension, fileKey);

                    photos.add(np);

//                    listOfLines.add(newPhotos.toString());
                }
                nestedPhotos.add(photos);
            }
            newPhotos.add(nestedPhotos);
        }
        return newPhotos;
    }

    public static String getNumber(int num, int length) {
        String number = String.valueOf(num);
        StringBuilder sb = new StringBuilder(number);
        for (int i = 0; i < length - String.valueOf(num).length(); i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    public static ArrayList<ArrayList<ArrayList<OldPhoto>>> splitByDateAndTime(ArrayList<ArrayList<OldPhoto>> oldPhotosNotSplittedBydate) {
        ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotosSplittdBydate = new ArrayList<>();
        for (int i = 0; i < oldPhotosNotSplittedBydate.size(); i++) {
            // sort by date
            ArrayList<OldPhoto> oldPhotosSortedByDate = sortByField(oldPhotosNotSplittedBydate.get(i), "0000-00-00");
            ArrayList<ArrayList<OldPhoto>> oldPhotosSortedAndSplittdByDate = new ArrayList<>();
            int startIndex = 0;
            for (int j = 0; j < oldPhotosSortedByDate.size(); j++) {
                String dateBefore = oldPhotosSortedByDate.get(j).date.substring(0, 4);
                if (j + 1 < oldPhotosSortedByDate.size()) {
                    String dateAfter = oldPhotosSortedByDate.get(j + 1).date.substring(0, 4);
                    if (!dateBefore.equals(dateAfter)) {
                        oldPhotosSortedAndSplittdByDate.add(sortByField(new ArrayList<>(oldPhotosSortedByDate.subList(startIndex, j + 1)), "00:00:00"));
                        startIndex = j + 1;
                    }
                }
            }
            oldPhotosSortedAndSplittdByDate.add(sortByField(new ArrayList<>(oldPhotosSortedByDate.subList(startIndex, oldPhotosSortedByDate.size())), "00:00:00"));
            oldPhotosSplittdBydate.add(new ArrayList<>(oldPhotosSortedAndSplittdByDate));
        }
        return oldPhotosSplittdBydate;
    }

    public static ArrayList<ArrayList<OldPhoto>> splitByCities(ArrayList<OldPhoto> oldPhotosNotSplittedNotSplittedByCity) {
        ArrayList<ArrayList<OldPhoto>> oldPhotosSplittd = new ArrayList<>();
        int strtIndex = 0;
        for (int i = 0; i < oldPhotosNotSplittedNotSplittedByCity.size(); i++) {
            String cityBefore = oldPhotosNotSplittedNotSplittedByCity.get(i).city;
            if (i + 1 < oldPhotosNotSplittedNotSplittedByCity.size()) {
                String cityAfter = oldPhotosNotSplittedNotSplittedByCity.get(i + 1).city;
                if (!cityBefore.equals(cityAfter)) {
                    oldPhotosSplittd.add(new ArrayList<>(oldPhotosNotSplittedNotSplittedByCity.subList(strtIndex, i + 1)));
                    strtIndex = i + 1;
                }
            }
        }
        oldPhotosSplittd.add(new ArrayList<>(oldPhotosNotSplittedNotSplittedByCity.subList(strtIndex, oldPhotosNotSplittedNotSplittedByCity.size())));
        return oldPhotosSplittd;
    }

    public static ArrayList<OldPhoto> sortByField(ArrayList<OldPhoto> oldPhotos, String field) {
        if (Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$").matcher(field).find()) { // this is date
            Collections.sort(oldPhotos, new Comparator<OldPhoto>() {
                @Override
                public int compare(OldPhoto o1, OldPhoto o2) {
                    return o1.date.compareTo(o2.date);
                }
            });
        } else if (Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$").matcher(field).find()) { // this is time
            Collections.sort(oldPhotos, new Comparator<OldPhoto>() {
                @Override
                public int compare(OldPhoto o1, OldPhoto o2) {
                    return o1.time.compareTo(o2.time);
                }
            });
        } else { // this must be city
            Collections.sort(oldPhotos, new Comparator<OldPhoto>() {
                @Override
                public int compare(OldPhoto o1, OldPhoto o2) {
                    return o1.city.compareTo(o2.city);
                }
            });
        }
        return oldPhotos;
    }

    public static String getDate(String line) {
        int firstComma = line.indexOf(",");
        int firstSpace = line.indexOf(" ");
        int secondSpace = line.indexOf(" ", firstSpace + 1);

        return line.substring(line.indexOf(",", firstComma + 1) + 2, line.indexOf(" ", secondSpace + 1));
    }

    public static String getTime(String line) {
        int firstSpace = line.indexOf(" ");
        int secondSpace = line.indexOf(" ", firstSpace + 1);
        return line.substring(line.indexOf(" ", secondSpace + 1));
    }

    public static String getExtension(String line) {
        return line.substring(line.indexOf("."), line.indexOf(","));
    }

    public static String getCity(String line) {
        int firstComma = line.indexOf(",");
        return line.substring(line.indexOf(" ") + 1, line.indexOf(",", firstComma + 1));
    }

    public static ArrayList<OldPhoto> readFile(String s) throws IOException {
        ArrayList<OldPhoto> oldPhotos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(s));
        ArrayList<String> lines = new ArrayList<>();
        String line = reader.readLine();
        int fileKey = 1;
        while (line != null) {
            lines.add(line);
            String date = getDate(line);
            String time = getTime(line);
            String extension = getExtension(line);
            String city = getCity(line);

            oldPhotos.add(new OldPhoto(date, time, city, extension, fileKey));

            line = reader.readLine();
            fileKey++;
        }
        return oldPhotos;
    }

    public static int getMax(ArrayList<OldPhoto> listOfOldPhotos) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < listOfOldPhotos.size(); i++) {
            if (listOfOldPhotos.get(i).fileKey > max) {
                max = listOfOldPhotos.get(i).fileKey;
            }
        }
        return max;
    }

    public static void solution(String S) {
        // write your code in Java SE 8

    }

    public static void main(String[] args) throws IOException {

//        int a [] = {1, 3, 6, 4, 1, 2};
//
//        System.out.println(solution(a));

        String s = "photo.jpg, Warsaw, 2013-10-23 14:08:15\n" +
                "john.png, London, 2015-06-20 15:13:22\n" +
                "john.jpg, London, 2016-06-20 15:13:22\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, London, 2013-10-23 17:20:10\n" +
                "a.jpeg, Warsaw, 2016-09-05 14:07:13\n" +
                "b.png, London, 2016-09-05 14:07:13\n" +
                "c.png, Warsaw, 2019-09-05 14:10:50\n" +
                "d.jpeg, Paris, 2018-09-05 14:07:13\n" +
                "e.png, Paris, 2018-09-05 14:07:14\n" +
                "f.jpg, Warsaw, 2017-10-05 14:07:13\n" +
                "j.png, Paris, 2011-11-05 14:07:13\n";
        ArrayList lines = readingStringLineByLine(s);

//        System.out.println(getNumber(2, 5));
//        System.out.println(s);
    }
}


