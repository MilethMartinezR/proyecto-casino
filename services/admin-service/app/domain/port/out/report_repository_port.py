from abc import ABC, abstractmethod


class EventPublisherPort(ABC):
    @abstractmethod
    async def publish(self, event: dict, topic: str) -> None:
        ...
