import { GoogleGenerativeAI } from '@google/generative-ai';
import type { Channel, MessageResponse, StreamChat } from 'stream-chat';

export class GeminiAIResponseHandler {
  private message_text = '';
  private chunk_counter = 0;

  constructor(
    private readonly genAI: GoogleGenerativeAI,
    private readonly model: any, // Ganti 'any' dengan tipe yang sesuai
    private readonly chatClient: StreamChat,
    private readonly channel: Channel,
    private readonly message: MessageResponse,
    private readonly userMessage: string,
  ) {
    this.chatClient.on('ai_indicator.stop', this.handleStopGenerating);
  }

  run = async () => {
    try {
      const result = await this.model.generateContent(this.userMessage);
      const response = await result.response;
      const text = response.text();

      await this.chatClient.partialUpdateMessage(this.message.id, {
        set: { text, generating: false },
      });
      await this.channel.sendEvent({
        type: 'ai_indicator.clear',
        cid: this.message.cid,
        message_id: this.message.id,
      });
    } catch (error) {
      console.error('Error generating content:', error);
      await this.handleError(error as Error);
    }
  };

  dispose = () => {
    this.chatClient.off('ai_indicator.stop', this.handleStopGenerating);
  };

  private handleStopGenerating = async () => {
    console.log('Stop generating');
    await this.chatClient.partialUpdateMessage(this.message.id, {
      set: { generating: false },
    });
    await this.channel.sendEvent({
      type: 'ai_indicator.clear',
      cid: this.message.cid,
      message_id: this.message.id,
    });
  };

  private handleError = async (error: Error) => {
    await this.channel.sendEvent({
      type: 'ai_indicator.update',
      ai_state: 'AI_STATE_ERROR',
      cid: this.message.cid,
      message_id: this.message.id,
    });
    await this.chatClient.partialUpdateMessage(this.message.id, {
      set: {
        text: 'Error generating the message',
        message: error.toString(),
        generating: false,
      },
    });
  };
}
