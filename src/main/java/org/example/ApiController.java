package org.example;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RestController
public class ApiController {

    @GetMapping("/fast")
    public String fast() {
        return "FAST OK";
    }

    // Chậm KHÔNG bulkhead -> GIỮ servlet thread 5s
    @GetMapping("/slow-no-bulkhead")
    public String slowNoBulkhead() {
        try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return "SLOW NOBH OK";
    }

    // Chậm CÓ bulkhead -> TRẢ servlet thread NGAY, chạy trong pool riêng
    @Bulkhead(name = "slowPool", type = Bulkhead.Type.THREADPOOL)
    @GetMapping("/slow")
    public CompletionStage<String> slow() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "SLOW BH OK";
        });
    }
}
