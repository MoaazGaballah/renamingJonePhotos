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

        public String toString(){
            return this.city + this.number + this.extension;
        }

        /*Comparator for sorting the list by roll no*/
        public static Comparator<newPhoto> newPhotoFileKeySorting = new Comparator<newPhoto>() {

            public int compare(newPhoto np1, newPhoto np2) {

                int fileKey1 = np1.fileKey;
                int fileKey2 = np2.fileKey;

                /*For ascending order*/
                return fileKey1 - fileKey2;

                /*For descending order*/
//                fileKey2 - fileKey1;
            }};
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

    public static void writeNewPhotosToNewList(ArrayList<newPhoto> newPhotos){
       sortNewPhotosByFileKey(newPhotos);
        for (int i = 0; i < newPhotos.size(); i++) {
            listOfLines.add(newPhotos.get(i).toString());
        }
    }

    public static ArrayList<newPhoto> sortNewPhotosByFileKey(ArrayList<newPhoto> newPhotos){
        Collections.sort(newPhotos, newPhoto.newPhotoFileKeySorting);
        return newPhotos;
    }

    public static ArrayList<newPhoto> getNewPhotos(ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotos) {
        ArrayList<newPhoto> newPhotos = new ArrayList<>();
        for (int i = 0; i < oldPhotos.size(); i++) {

            // this count will be return to one for each city arraylist
            int count = 1;

            for (int j = 0; j < oldPhotos.get(i).size(); j++) {

                // in cities level we are going to specify a number according to city array length
                int length = String.valueOf(oldPhotos.get(i).get(j).size()).length();

                for (int k = 0; k < oldPhotos.get(i).get(j).size(); k++) {
                    String number = getNumber(count, length);
                    String city = oldPhotos.get(i).get(j).get(k).city;
                    String extension = oldPhotos.get(i).get(j).get(k).extension;
                    int fileKey = oldPhotos.get(i).get(j).get(k).fileKey;
                    newPhoto np = new newPhoto(city, number, extension, fileKey);

                    newPhotos.add(np);

                    count++;
                }
            }
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

    public static ArrayList<ArrayList<ArrayList<OldPhoto>>> splitByDate(ArrayList<ArrayList<OldPhoto>> oldPhotosNotSplittedBydate) {
        ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotosSplittdBydate = new ArrayList<>();
        for (int i = 0; i < oldPhotosNotSplittedBydate.size(); i++) {
            // sort by date
            ArrayList<OldPhoto> oldPhotosSortedByDate = sortByField(oldPhotosNotSplittedBydate.get(i), "0000-00-00");
            ArrayList<ArrayList<OldPhoto>> oldPhotosSortedAndSplittdByDate = new ArrayList<>();
            int startIndex = 0;
            for (int j = 0; j < oldPhotosSortedByDate.size(); j++) {
                String dateBefore = oldPhotosSortedByDate.get(j).date;
                if (j + 1 < oldPhotosSortedByDate.size()) {
                    String dateAfter = oldPhotosSortedByDate.get(j + 1).date;
                    if (!dateBefore.equals(dateAfter)) {
                        oldPhotosSortedAndSplittdByDate.add(sortByField(new ArrayList<>(oldPhotosSortedByDate.subList(startIndex, j + 1)),"00:00:00"));
                        startIndex = j + 1;
                    }
                }
            }
            oldPhotosSortedAndSplittdByDate.add(sortByField(new ArrayList<>(oldPhotosSortedByDate.subList(startIndex, oldPhotosSortedByDate.size())),"00:00:00"));
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


    public static ArrayList<String> solution(String S) throws IOException{
        // write your code in Java SE 8

        // 1 - read file
        // 2 - create an old photos and get cities, date, time, and extension
        // 3 - sort photos by city, split into nested arraylist (each city is in a list alone)
        // 4 - sort photos by date, split into nested arraylist (each date is in a list alone)
        // 5 - sort photos in the latest list by time
        // 6 - create a new photo list with old properties and generating a new number
        // 7 - sort the latest list with filekey and write them as string into a global string list

        ArrayList<OldPhoto> oldPhotos = readFile(S);

        // sort by city
        sortByField(oldPhotos, " ");

        // group cities together
        ArrayList<ArrayList<OldPhoto>> oldPhotosSplittedByCities = splitByCities(oldPhotos);

        // sort by date
        ArrayList<ArrayList<ArrayList<OldPhoto>>> oldPhotosSplittedAndSortedByDateAndTime = splitByDate(oldPhotosSplittedByCities);

        // get a list for new photos
        ArrayList<newPhoto> newPhotos = getNewPhotos(oldPhotosSplittedAndSortedByDateAndTime);

        // write to list of string
        writeNewPhotosToNewList(newPhotos);

        // return the result
        return listOfLines;

    }

    public static void main(String[] args) throws IOException {

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
                ArrayList lines = solution(s);

        for (int i = 0; i < lines.size(); i++) {
            System.out.println(lines.get(i));
        }
    }
}


