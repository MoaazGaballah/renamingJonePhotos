import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class solutionV2 {

    public static class NewPhoto {
        String city;
        String number;
        String extension;
        int fileKey;

        public NewPhoto(String city, String number, String extension, int fileKey) {
            this.city = city;
            this.number = number;
            this.extension = extension;
            this.fileKey = fileKey;
        }

        public String toString() {
            return this.city + this.number + this.extension;
        }
    }

    public static class OldPhoto {
        String timestamp;
        String city;
        String extension;
        int fileKey;

        public OldPhoto(String timestamp, String city, String extension, int fileKey) {
            this.timestamp = timestamp;
            this.city = city;
            this.extension = extension;
            this.fileKey = fileKey;
        }
    }

    public static void copyNewPhotoListToStringList (ArrayList<NewPhoto> newPhotos){
        for (int i = 0; i < newPhotos.size(); i++) {
            listOfLines.add(newPhotos.get(i).toString());
        }
    }


    public static ArrayList<NewPhoto> sortByFilekey(ArrayList<NewPhoto> newPhotos) {
        Collections.sort(newPhotos, new Comparator<NewPhoto>() {
            @Override
            public int compare(NewPhoto o1, NewPhoto o2) {
                return o1.fileKey - o2.fileKey;
            }
        });
        return newPhotos;
    }

    public static void writeToNewPhotoList(ArrayList<OldPhoto> oldPhotos){

        int length = String.valueOf(oldPhotos.size()).length();

        for (int i = 0; i < oldPhotos.size(); i++) {

            OldPhoto oldPhoto = oldPhotos.get(i);

            String number = getNumber(i + 1,length);

            NewPhoto newPhoto = new NewPhoto(oldPhoto.city, number, oldPhoto.extension, oldPhoto.fileKey);

            newPhotos.add(newPhoto);
        }
    }

    public static ArrayList<String> listOfLines = new ArrayList<>();

    public static ArrayList<NewPhoto> newPhotos = new ArrayList<>();

    public static String getNumber(int num, int length) {
        String number = String.valueOf(num);
        StringBuilder sb = new StringBuilder(number);
        for (int i = 0; i < length - String.valueOf(num).length(); i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    public static ArrayList<OldPhoto> sortByTimestamp(ArrayList<OldPhoto> oldPhotos) {
        Collections.sort(oldPhotos, new Comparator<OldPhoto>() {
            @Override
            public int compare(OldPhoto o1, OldPhoto o2) {
                return o1.timestamp.compareTo(o2.timestamp);
            }
        });
        return oldPhotos;
    }

    public static String getTimestamp(String line) {
        int firstSpace = line.indexOf(" ");
        int secondSpace = line.indexOf(" ", firstSpace + 1);
        return line.substring(secondSpace + 1, line.contains("\n") ? line.indexOf("\n") : line.length());
    }

    public static String getExtension(String line) {
        return line.substring(line.indexOf("."), line.indexOf(","));
    }

    public static String getCity(String line) {
        int firstComma = line.indexOf(",");
        return line.substring(line.indexOf(" ") + 1, line.indexOf(",", firstComma + 1));
    }

    public static Map<String, ArrayList<OldPhoto>> readFile(String s) throws IOException {

        Map<String, ArrayList<OldPhoto>> oldPhotosMap = new HashMap<>();

        BufferedReader reader = new BufferedReader(new StringReader(s));
        ArrayList<String> lines = new ArrayList<>();
        String line = reader.readLine();
        int fileKey = 1;
        while (line != null) {
            lines.add(line);
            String timestamp = getTimestamp(line);
            String extension = getExtension(line);
            String city = getCity(line);

            OldPhoto oldPhoto = new OldPhoto(timestamp, city, extension, fileKey);

            oldPhotosMap.computeIfAbsent(city, k -> new ArrayList<>()).add(oldPhoto);

            line = reader.readLine();

            fileKey++;
        }
        return oldPhotosMap;
    }


    public static ArrayList<String> solution(String S) throws IOException {
        // write your code in Java SE 8

        // 1 - read file to a new map<city, Arraylist<OldPhoto>>
        // 2 - sort all maps value (arraylists) according to timestamp
        // 3 - giving a number and create a new photos list
        // 4 - sort new photos list according to file key
        // 5 - copy new photos list content to String list

        // first step (read file into a map)
        Map<String, ArrayList<OldPhoto>> oldPhotosMap = readFile(S);


        for (ArrayList<OldPhoto> elementCityList : oldPhotosMap.values()) {

            // second step(sort photos according to timestamp)
            sortByTimestamp(elementCityList);

            // third step(creating new photos list)
            writeToNewPhotoList(elementCityList);
        }

        // forth step(sorting new photos list according to file key)
        sortByFilekey(newPhotos);

        // fifth step(copy new photos list content to String list)
        copyNewPhotoListToStringList(newPhotos);

        // return the result
        return listOfLines;

    }

    public static void main(String[] args) throws IOException {

        String s = "photo.jpg, Warsaw, 2013-10-23 14:08:15\n" +
                "john.png, London, 2015-06-20 15:13:22\n" +
                "john.jpg, London, 2016-06-20 15:13:22\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
                "john.png, Paris, 2016-12-20 12:16:23\n" +
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


