import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * This is the entry point for the command line application.
 * This class also provides a testing driver for the dependent classes.
 * 
 * @author Baile Benedicte
 * version 1.0.0
 * 12/07/2022
 * 
 */
public class ContactsApp {

    /**
     * The collection of contacts managed by this application.
     */
    private final BinarySearchTreeMap<String, HashMap<ContactsApp.Communications, String>> contacts = new BinarySearchTreeMap<>();
    private Integer put;

    /**
     * Assert that the given condition is true.
     *
     * @param condition   The given condition expected to be true.
     * @param message     The test condition message that can also have the list of
     *                    information parameters
     *                    formatted into the resulting string.
     * @param information Extra parameters to be formatted into the message string
     * @return true when condition is true, otherwise false.
     */
    private static boolean assertTrue(boolean condition, String message, Object... information) {
        System.out.print(condition ? "✓  Passed " : "☠  Failed ");
        System.out.format(message + "%n", information);

        return condition;
    }

    /**
     * The application entry point.
     *
     * @param args The command line arguments. If no argument provided, then the
     *             application menu is shown.
     *             If the argument is "-test", then the unit tests are executed and
     *             the application exists.
     */
    public static void main(String... args) {
        ContactsApp contactsApp = new ContactsApp();

        // Determine if testing mode
        if (args.length == 1 && args[0].trim().equalsIgnoreCase("-test")) {
            System.exit(contactsApp.testBinarySearchTreeMap() && contactsApp.testCRUD() ? 0 : -1);
        }

        contactsApp.menu();
    }

    /**
     * Converts a string of comma separated keys and values of communication options
     * into a
     * HashMap of the communication options. For instance the string "email:
     * me@trinity.com, link: me, snap: @me" is
     * converted into a map of keys and values where the keys are the Communications
     * enums (EMAIL, SNAPCHAT, etc),
     * and the values are the associated communication ids on that platform, eg.
     * "me@trinity.com".
     *
     * @param platforms The string representing the options
     * @return The parsed communication options as a HashMap collection
     */
    private HashMap<Communications, String> parseCommunications(String platforms) throws IllegalArgumentException {
        HashMap<Communications, String> communications = new HashMap<>();

        // Parse the string at the commas into an array of tokens
        for (String platform : platforms.split(",")) {

            // Parse each token at the colon, to separate the communication types and its
            // associated id
            String[] platformKeyValue = platform.split(":");

            // Array must have two parts, e.g. "email" and "me@trinity.com"
            if (platformKeyValue.length == 2) {
                // Given the token like "g", find the matching enum, such as "GITHUB", assumes
                // first letter is unique
                String platformLetter = "" + platformKeyValue[0].trim().toUpperCase().charAt(0);
                Communications com = Arrays.stream(
                        Communications.values())
                        .filter(communication -> communication.name().startsWith(platformLetter)).findFirst()
                        .orElseThrow(IllegalArgumentException::new);
                communications.put(com, platformKeyValue[1].trim()); // Add to map "EMAIL" associated with
                                                                     // "me@trinity.com"
            }
        }

        return communications;
    }

    /**
     * Unit Tests for BinarySearchTreeMap. All output is clean and legible.
     * <p>
     * 1. Insert all the samples into the searchables collection using the samples
     * as keys and arbitrary Integers as values.
     * 2. Print and verify all entries as legible key-value pairs using the method
     * entrySet().
     * 3. Test the methods get(), put() and remove().
     * 4. Print and verify all values using the method values().
     * 5. Print and verify all keys using the method keySet().
     *
     * @return true when all tests pass.
     */
    private boolean testBinarySearchTreeMap() {

        BinarySearchTreeMap<String, Integer> searchables = new BinarySearchTreeMap<>();
        String[] samples = { "gamma", "phi", "beta", "alpha", "delta", "lambda", "epsilon", "zeta" };
        Integer[] values = new Integer[samples.length];

        // Set up: Insert all samples into collection
        Random random = new Random();
        for (int i = 0; i < samples.length; i++) {
            values[i] = random.nextInt(999);
            searchables.put(samples[i], values[i]);
        }

        // Test 1: All items inserted into tree
        int expectedSizeWithSentinels = (samples.length * 2) + 1;
        boolean pass = assertTrue(searchables.size == expectedSizeWithSentinels,
                "Test 1: All items (including sentinels) have been added to tree as count is %d.",
                expectedSizeWithSentinels);
        String phiToken = "(" + samples[1] + ", " + values[1] + ")";
        pass = pass && assertTrue(searchables.toString().contains(phiToken), "Test 1: Text output contains token: %s",
                phiToken);

        // Test 2: Print all entries legibly
        String allEntriesDump = searchables.toString(); // create a string to hold all entries of map searchables
        System.out.format("All Entries: %n%s%n", allEntriesDump);
        pass = pass && assertTrue(allEntriesDump.length() > 100,
                "Test 2: The text output of the tree has an expected length: %d.", allEntriesDump.length()); // check if given condition is
                                                                                                            // true and mark test as pass if so


        // Test 3: methods get(), put() and remove()
        // use of get() method
        for (int i = 0; i < samples.length; i++) { // loop over elements of array samples
            String key = samples[i];
            pass = pass && assertTrue(searchables.get(key).equals(values[i]),
                    "Test 3: The key '%s' has the value '%s'.", key, values[i]);// check if given condition is true and
                                                                                // mark test as pass if so
        }
        // use put() method
        put = searchables.put("life", 42); // add entry with key = life and value= 42 to the tree
        pass = pass && assertTrue(searchables.get("life").equals(42), "Test 3: The key 'life' has the value '42'."); // check if given condition is
                                                                                                                     // true and mark test as pass if so

        // use of remove() method
        String lifeToken = "(life, 42)";
        pass = pass && assertTrue(searchables.toString().contains(lifeToken),
                "Test 3: Before remove collection contains token: '%s'.", lifeToken); // check if given condition is
                                                                                      // true and mark test as pass if so
                                     

        searchables.remove("life"); // remove the value coresponding to key = life
        pass = pass && assertTrue(!searchables.toString().contains(lifeToken),
                "Test 3: After remove colection does not contains token '%s'.", lifeToken); // check if given condition
                                                                                            // is true and mark test as
                                                                                            // pass if so

        pass = pass && assertTrue(searchables.get(lifeToken) == null,
                "Test 3: Search for '%s' after removal was null.", lifeToken); // check if given condition is true and
                                                                               // mark test as pass if so

        pass = pass && assertTrue(searchables.get("") == null,
                "Test 3: Search for blank key after removal was null.", searchables.get(""));// check if given condition
                                                                                             // is true and mark test as
                                                                                             // pass if so

        // Test 4: Print and verify all values using the method values().
        for (Integer value : searchables.values()) { // loop over values in searchables
            pass = pass && assertTrue(Arrays.asList(values).contains(value),
                    "Test 4: Verify value(), the value '%d' was expected.", value);// check if given condition is true
                                                                                   // and mark test as pass if so
        }

        // Test 5: Print and verify all keys using the method keySet().
        for (String key : searchables.keySet()) { // loop over keys in searchables
            pass = pass && assertTrue(Arrays.asList(samples).contains(key),
                    "Test 4: Verify value(), the value '%s' was expected.", key); // check if given condition is true
                                                                                  // and mark test as pass if so
        }

        return assertTrue(pass, "All tests for BinarySearchTreeMap");
    }

    /**
     * Unit Tests for menu selections. All output is clean and legible.
     *
     * @return true when all tests pass.
     */
    private boolean testCRUD() {

        // Test: Search for non-existing entry
        String unknownSoldier = "Waldo";
        boolean pass = assertTrue(contacts.get(unknownSoldier) == null, "'%s' was not found.", unknownSoldier);

        // Test: Add entry and get the value
        String johnMuirName = "Muir, John";
        contacts.put(johnMuirName, parseCommunications("email: john.muir@sierraclub.org, linkedin: johnmuir"));
        String johnMuirEmail = contacts.get(johnMuirName).get(ContactsApp.Communications.EMAIL);
        pass = pass
                && assertTrue(johnMuirEmail.length() > 0, "Contact '%s' has email `%s`.", johnMuirName, johnMuirEmail);

        contacts.put(johnMuirName, parseCommunications("email: john.muir@sierraclub.org, linkedin: johnmuir"));

        // Add more nice people (feel free to replace with your own data)
        String[] names = {
                "Elizabeth Wathuti",
                "Shiva, Vandana",
                "Ceesay, Isatou",
                "LaDuke, Winona",
                "Maathai, Wangari" };
        String[] coms = {
                "email: lizwathuti@gmail.com, website: linktr.ee/lizwathuti, linkedin: elizabeth-wathuti-8415a299",
                "web: vandanashiva.com",
                "linkedin: isatou-ceesay-4a837216",
                "li: winona-laduke-71861818, w: https://en.wikipedia.org/wiki/Winona_LaDuke",
                "li: wanjira-mathai-1b561ab, mobile: 44-023-233-2323"
        };

        // Add above data to contacts
        for (int i = 0; i < names.length; i++) {
            contacts.put(names[i], parseCommunications(coms[i]));
        }

        // Test: Remove contact
        contacts.remove(johnMuirName);
        pass = pass && assertTrue(contacts.get(johnMuirName) == null,
                "Contact %s was removed.", johnMuirName);

        // Test: Form 1: List all information for all contacts
        pass = pass && assertTrue(contacts.toString().length() > 300,
                "Form 1: Raw list has expected length: %d. Content: %n%s", contacts.toString().length(),
                contacts.toString()); // check if given condition is true and mark test as pass if so

        // Test: Form 2: List all information for all contacts
        String allContacts = listAllContacts(); // create a string to hold all contact info
        pass = pass && assertTrue(allContacts.length() > 300,
                "Form 2: Formatted list has an expected length: %d. Content: %n%s", allContacts.length(), allContacts); // check if given condition is
                                                                                                                        // true and mark test as pass if so


        // Test: List all contact names
        String allNames = listAllContactNames(); // create a string to hold all contact names
        pass = pass && assertTrue(allNames.length() > 100, "All contact names: %d. Content: %n%s", allNames.length(),
                allNames);// check if given condition is true and mark test as pass if so

        // Test: List all contact communications
        String allContactCommunications = listAllContactCommunications(); // create a string to hold all contact
                                                                          // communications
        pass = pass && assertTrue(allContactCommunications.length() > 280,
                "All values of all contacts have expected length: %d. Content: %n%s", allContactCommunications.length(),
                allContactCommunications); // check if given condition is true and mark test as pass if so

        return pass;
    }

    /**
     * Present the application menu.
     */
    private void menuPresent() {
        System.out.println("\nContact Manager Menu");
        System.out.println("--------------------");
        System.out.println("1 - Search for a contact");
        System.out.println("2 - Add a new contact");
        System.out.println("3 - Remove contact");
        System.out.println("4 - List all information for all contacts");
        System.out.println("5 - List all contact names");
        System.out.println("6 - List all contact communications");
        System.out.println("---");
        System.out.println("7 - End this contact manager session.");

        System.out.print("\nMenu choice: ");
    }

    /**
     * Present the application menu and respond to selections.
     */
    public void menu() {
        for (;;) {
            menuPresent();
            Scanner input = new Scanner(System.in);

            int selection = 0;
            try {
                selection = input.nextInt();
                input.nextLine(); // Consume the enter key after the number
            } catch (InputMismatchException ex) {
                selection = 0;
            }
            switch (selection) {
                case 1:
                    // Search for a contact
                    searchForContact(input);
                    break;
                case 2:
                    // Add a new contact
                    addContact(input);
                    break;
                case 3:
                    // remove a contact
                    removeContact(input);
                    break;
                case 4:
                    // List all information for all contacts
                    System.out.println(listAllContacts());
                    break;
                case 5:
                    // List all contact names
                    System.out.println(listAllContactNames());
                    break;
                case 6:
                    // List all contact communications
                    System.out.println(listAllContactCommunications());
                    break;
                case 7:
                    System.exit(0);
                default:
                    System.out.println("Select a menu choice from 1 to 7.");
            }
        }
    }

    /**
     * Prompt user for a contact name and either show the information of the found
     * contact or report the contact
     * was not found with the given name.
     *
     * @param input The input console stream
     */
    private void searchForContact(Scanner input) {
        System.out.println("Search for contact:");
        String name = promptFullName(input);
        if (name.isBlank()) {
            System.out.format("The contact's first or last name was invalid.%n", name);
            return;
        }
        if (contacts.get(name) == null) { // check if value is null
            System.out.format("No contact entry found for '%s'.%n", name);
        } else {
            System.out.format("Contact information for '%s' is '%s'.%n", name, contacts.get(name)); // print name and value
                                                                                           // associated with the key
        }
    }

    /**
     * Prompt user for a contact name and contact information. If the contact
     * exists, user is prompted to confirm
     * the update. If the input is valid, the contact is either added or updated.
     *
     * @param input The input console stream
     */
    private void addContact(Scanner input) {
        System.out.println("Add contact:");
        String name = promptFullName(input);
        if (name.isBlank()) {
            System.out.format("The contact's first or last name was invalid.%n", name);
            return;
        }
        System.out.println("  Communication options example: website: www.oceanfutures.org, m: 805-899-8899");
        System.out.print("  Communication options: ");
        String coms = input.nextLine().trim();

        HashMap<Communications, String> comsCollection;
        try {
            comsCollection = parseCommunications(String.join(", ", coms));
        } catch (IllegalArgumentException ex) {
            System.out.format("Media option in '%s' not recognized.%n", coms);
            return;
        }
        if (contacts.get(name) == null) { // check if value is null
            contacts.put(name, comsCollection); // add entry (name and communication details) to the contacts
            System.out.format("Contact added: %s: %s.%n", name, comsCollection);
        } else {
            System.out.println("Update existing contact(y/n): "); // ask for user input
            String overwrite = input.nextLine().trim(); // scan in user input into a string
            if (overwrite.trim().toUpperCase().charAt(0) == 'Y') { // check if user chose yes or no
                contacts.put(name, comsCollection); // update contact information
                System.out.format("Contact Updated: %s: %s.%n", name, comsCollection);
            }
        }
    }

    /**
     * Prompt user for a contact name and either remove the contact or report the
     * contact
     * was not found with the given name.
     *
     * @param input The input console stream
     */
    private void removeContact(java.util.Scanner input) {
        System.out.println("Remove contact:");
        String name = promptFullName(input);
        if (name.isBlank()) {
            System.out.format("The contact's first or last name was invalid.%n", name);
            return;
        }
        if (contacts.get(name) == null) { // check if value is null
            System.out.format("No contact entry found for '%s'.%n", name);
        } else {
            contacts.remove(name); // remove the values assiocated with name in the hashmap
            System.out.format("The Contact '%s' has been removed.%n", name);
        }
    }

    /**
     * A formatted string for the console containing the list of the contact names
     * in alphabetical order with
     * their associated communication options.
     *
     * @return A string containing the list of the contact names in alphabetical
     *         order.
     */
    private String listAllContacts() {
        List<String> elementsAsText = new ArrayList<>(contacts.size());

        for (Entry<String, HashMap<ContactsApp.Communications, String>> entry : contacts.entrySet()) {
            elementsAsText.add(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }
        // Return results without extra comma at end
        return "\n" +
                "All Contacts" + "\n" +
                "------------" + "\n" +
                String.join("\n", elementsAsText) +
                "\n";
    }

    /**
     * A formatted string for the console containing the list of the contact names
     * in alphabetical order.
     *
     * @return A string containing the list of the contact names in alphabetical
     *         order.
     */
    private String listAllContactNames() {
        return "\n" +
                "All Contacts Names" + "\n" +
                "------------" + "\n" +
                String.join("\n", contacts.keySet()) +
                "\n"; // tranform entire keyset into a string
    }

    /**
     * A formatted string for the console containing the list of the contact names
     * in alphabetical order.
     *
     * @return A string containing the list of the contact names in alphabetical
     *         order.
     */
    private String listAllContactCommunications() {
        List<String> elementsAsText = new ArrayList<>(contacts.size()); // create an arrayList
        for (HashMap<ContactsApp.Communications, String> map : contacts.values()) { // loop over the values in contacts
            elementsAsText.add(String.format("%s", map.toString())); // add each element in the arrayList
        }
        return "\n" +
                "All Contacts Communications" + "\n" +
                "------------" + "\n" +
                String.join("\n", elementsAsText) +
                "\n";
    }

    /**
     * Prompts user for contact first and last name.
     *
     * @param input The input console stream
     * @return The first and last name formatted as 'last, first'. Returns blank if
     *         name is invalid.
     */
    private String promptFullName(java.util.Scanner input) {
        System.out.print("  First name: ");
        String firstName = input.nextLine().trim();
        System.out.print("  Last name: ");
        String lastName = input.nextLine().trim();

        return firstName.isEmpty() || lastName.isEmpty() ? "" : lastName + ", " + firstName;
    }

    /**
     * A list of example communication options that a contact may have. Other
     * options can be added.
     */
    private enum Communications {
        EMAIL,
        MOBILE,
        GITHUB,
        INSTAGRAM,
        LINKEDIN,
        WEBSITE,
        SNAPCHAT
    }
}
