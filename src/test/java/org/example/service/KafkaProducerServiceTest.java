package org.example.service;


@SpringBootTest(
    classes = {KafkaProducerService.class},
    properties = {"topic-to-send-message=your-topic-config"}
)
@Import({KafkaAutoConfiguration.class, ObjectMapperTestConfig.class})
@Testcontainers
class KafkaProducerServiceTest {

  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private KafkaProducerService kafkaProducerService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() {
    DtoMessage testDtoMessage = new DtoMessage();

    assertDoesNotThrow(() -> kafkaProducerService.sendMessage(testDtoMessage));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          DtoMessage message = objectMapper.readValue(record.value(), DtoMessage.class);
          assertEquals(testDtoMessage, message);
        }
    );
  }
}

public class KafkaTestConsumer {

  private final KafkaConsumer<String, String> consumer;

  public KafkaTestConsumer(String bootstrapServers, String groupId) {
    Properties props = new Properties();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    this.consumer = new KafkaConsumer<>(props);
  }

  public void subscribe(List<String> topics) {
    consumer.subscribe(topics);
  }

  public ConsumerRecords<String, String> poll() {
    return consumer.poll(Duration.ofSeconds(5));
  }

}
