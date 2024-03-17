package xml.parse.api;


public interface Parser {
    <T> T parse();
    <T> T getObject();
}
