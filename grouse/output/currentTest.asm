        Label        -mem-manager-initialize   
        DLabel       $heap-start-ptr           
        DataZ        4                         
        DLabel       $heap-after-ptr           
        DataZ        4                         
        DLabel       $heap-first-free          
        DataZ        4                         
        DLabel       $heap-next-record-num     
        DataZ        4                         
        DLabel       $mmgr-newblock-block      
        DataZ        4                         
        DLabel       $mmgr-newblock-size       
        DataZ        4                         
        PushD        $heap-memory              
        Duplicate                              
        PushD        $heap-start-ptr           
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        Jump         $$main                    
        DLabel       $eat-location-zero        
        DataZ        8                         
        DLabel       $print-format-integer     
        DataC        37                        %% "%d"
        DataC        100                       
        DataC        0                         
        DLabel       $print-format-float       
        DataC        37                        %% "%g"
        DataC        103                       
        DataC        0                         
        DLabel       $print-format-character   
        DataC        37                        %% "%c"
        DataC        99                        
        DataC        0                         
        DLabel       $print-format-string      
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-boolean     
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-newline     
        DataC        10                        %% "\n"
        DataC        0                         
        DLabel       $print-format-separator   
        DataC        32                        %% " "
        DataC        0                         
        DLabel       $boolean-true-string      
        DataC        116                       %% "true"
        DataC        114                       
        DataC        117                       
        DataC        101                       
        DataC        0                         
        DLabel       $boolean-false-string     
        DataC        102                       %% "false"
        DataC        97                        
        DataC        108                       
        DataC        115                       
        DataC        101                       
        DataC        0                         
        DLabel       $errors-general-message   
        DataC        82                        %% "Runtime error: %s\n"
        DataC        117                       
        DataC        110                       
        DataC        116                       
        DataC        105                       
        DataC        109                       
        DataC        101                       
        DataC        32                        
        DataC        101                       
        DataC        114                       
        DataC        114                       
        DataC        111                       
        DataC        114                       
        DataC        58                        
        DataC        32                        
        DataC        37                        
        DataC        115                       
        DataC        10                        
        DataC        0                         
        Label        $$general-runtime-error   
        PushD        $errors-general-message   
        Printf                                 
        Halt                                   
        DLabel       $errors-num-divide-by-zero 
        DataC        99                        %% "cant divide by zero"
        DataC        97                        
        DataC        110                       
        DataC        116                       
        DataC        32                        
        DataC        100                       
        DataC        105                       
        DataC        118                       
        DataC        105                       
        DataC        100                       
        DataC        101                       
        DataC        32                        
        DataC        98                        
        DataC        121                       
        DataC        32                        
        DataC        122                       
        DataC        101                       
        DataC        114                       
        DataC        111                       
        DataC        0                         
        Label        $$i-divide-by-zero        
        PushD        $errors-num-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $usable-memory-start      
        DLabel       $global-memory-block      
        DataZ        32                        
        Label        $$main                    
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% strOut
        PushI        1                         
        PushI        4                         
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% x
        DLabel       -str-constant-1           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        7                         
        DataC        100                       %% "ddasdsa"
        DataC        100                       
        DataC        97                        
        DataC        115                       
        DataC        100                       
        DataC        115                       
        DataC        97                        
        DataC        0                         
        PushD        -str-constant-1           
        StoreI                                 
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% str1
        DLabel       -str-constant-2           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        5                         
        DataC        116                       %% "true2"
        DataC        114                       
        DataC        117                       
        DataC        101                       
        DataC        50                        
        DataC        0                         
        PushD        -str-constant-2           
        StoreI                                 
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% str2
        DLabel       -str-constant-3           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        5                         
        DataC        49                        %% "1 + 3"
        DataC        32                        
        DataC        43                        
        DataC        32                        
        DataC        51                        
        DataC        0                         
        PushD        -str-constant-3           
        StoreI                                 
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% str3
        DLabel       -str-constant-4           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        5                         
        DataC        49                        %% "1 + 3"
        DataC        32                        
        DataC        43                        
        DataC        32                        
        DataC        51                        
        DataC        0                         
        PushD        -str-constant-4           
        StoreI                                 
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% x1
        PushI        2                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        24                        
        Add                                    %% x2
        PushI        3                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        28                        
        Add                                    %% x3
        PushI        2                         
        StoreI                                 
        Label        -if-statement-7           
        PushI        1                         
        JumpFalse    -if-else-7                
        DLabel       -str-constant-5           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        19                        
        DataC        73                        %% "INSIDE IF STATEMENT"
        DataC        78                        
        DataC        83                        
        DataC        73                        
        DataC        68                        
        DataC        69                        
        DataC        32                        
        DataC        73                        
        DataC        70                        
        DataC        32                        
        DataC        83                        
        DataC        84                        
        DataC        65                        
        DataC        84                        
        DataC        69                        
        DataC        77                        
        DataC        69                        
        DataC        78                        
        DataC        84                        
        DataC        0                         
        PushD        -str-constant-5           
        PushI        13                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% str1
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% str2
        LoadI                                  
        StoreI                                 
        DLabel       -str-constant-6           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        14                        
        DataC        82                        %% "RECORD NUMBER "
        DataC        69                        
        DataC        67                        
        DataC        79                        
        DataC        82                        
        DataC        68                        
        DataC        32                        
        DataC        78                        
        DataC        85                        
        DataC        77                        
        DataC        66                        
        DataC        69                        
        DataC        82                        
        DataC        32                        
        DataC        0                         
        PushD        -str-constant-6           
        PushI        13                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% str1
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Jump         -if-end-7                 
        Label        -if-else-7                
        Label        -if-end-7                 
        PushI        2                         
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% x1
        LoadI                                  
        Exchange                               
        PushD        $global-memory-block      
        PushI        24                        
        Add                                    %% x2
        LoadI                                  
        Exchange                               
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% x1
        LoadI                                  
        Exchange                               
        Call         -mem-manager-diagnostics  
        DLabel       -str-constant-8           
        DataI        10                        
        DataI        5                         
        DataC        0                         
        DataI        3                         
        DataC        88                        %% "X: "
        DataC        58                        
        DataC        32                        
        DataC        0                         
        PushD        -str-constant-8           
        PushI        13                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% x
        LoadI                                  
        PushI        13                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Halt                                   
        Label        -mem-manager-make-tags    
        DLabel       $mmgr-tags-size           
        DataZ        4                         
        DLabel       $mmgr-tags-start          
        DataZ        4                         
        DLabel       $mmgr-tags-available      
        DataZ        4                         
        DLabel       $mmgr-tags-nextptr        
        DataZ        4                         
        DLabel       $mmgr-tags-prevptr        
        DataZ        4                         
        DLabel       $mmgr-tags-return         
        DataZ        4                         
        PushD        $mmgr-tags-return         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-size           
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-start          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-available      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-nextptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-nextptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-return         
        LoadI                                  
        Return                                 
        Label        -mem-manager-one-tag      
        DLabel       $mmgr-onetag-return       
        DataZ        4                         
        DLabel       $mmgr-onetag-location     
        DataZ        4                         
        DLabel       $mmgr-onetag-available    
        DataZ        4                         
        DLabel       $mmgr-onetag-size         
        DataZ        4                         
        DLabel       $mmgr-onetag-pointer      
        DataZ        4                         
        PushD        $mmgr-onetag-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        4                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-onetag-return       
        LoadI                                  
        Return                                 
        Label        -mem-manager-allocate     
        DLabel       $mmgr-alloc-return        
        DataZ        4                         
        DLabel       $mmgr-alloc-size          
        DataZ        4                         
        DLabel       $mmgr-alloc-current-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-size 
        DataZ        4                         
        PushD        $mmgr-alloc-return        
        Exchange                               
        StoreI                                 
        PushI        18                        
        Add                                    
        PushI        4                         
        Add                                    
        PushD        $mmgr-alloc-size          
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-process-current 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        JumpFalse    -mmgr-alloc-no-block-works 
        Label        -mmgr-alloc-test-block    
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        1                         
        Add                                    
        JumpPos      -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Jump         -mmgr-alloc-process-current 
        Label        -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Call         -mem-manager-remove-block 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        26                        
        Subtract                               
        JumpNeg      -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Add                                    
        PushD        $mmgr-alloc-remainder-block 
        Exchange                               
        StoreI                                 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        Exchange                               
        Subtract                               
        PushD        $mmgr-alloc-remainder-size 
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushI        0                         
        PushI        0                         
        PushI        1                         
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushD        $mmgr-alloc-remainder-size 
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        Call         -mem-manager-deallocate   
        Jump         -mmgr-alloc-return-userblock 
        Label        -mmgr-alloc-no-block-works 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-newblock-size       
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        LoadI                                  
        PushD        $mmgr-newblock-block      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-newblock-size       
        LoadI                                  
        PushD        $heap-after-ptr           
        LoadI                                  
        Add                                    
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-newblock-size       
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        Duplicate                              
        PushD        $heap-next-record-num     
        LoadI                                  
        StoreI                                 
        PushI        1                         
        PushD        $heap-next-record-num     
        LoadI                                  
        Add                                    
        PushD        $heap-next-record-num     
        Exchange                               
        StoreI                                 
        PushI        4                         
        Add                                    
        PushD        $mmgr-alloc-return        
        LoadI                                  
        Return                                 
        Label        -mem-manager-deallocate   
        DLabel       $mmgr-dealloc-return      
        DataZ        4                         
        DLabel       $mmgr-dealloc-block       
        DataZ        4                         
        PushD        $mmgr-dealloc-return      
        Exchange                               
        StoreI                                 
        PushI        4                         
        Subtract                               
        PushI        9                         
        Subtract                               
        PushD        $mmgr-dealloc-block       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-dealloc-return      
        LoadI                                  
        Return                                 
        Label        -mem-manager-remove-block 
        DLabel       $mmgr-remove-return       
        DataZ        4                         
        DLabel       $mmgr-remove-block        
        DataZ        4                         
        DLabel       $mmgr-remove-prev         
        DataZ        4                         
        DLabel       $mmgr-remove-next         
        DataZ        4                         
        PushD        $mmgr-remove-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-prev         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-next         
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-prev 
        PushD        $mmgr-remove-prev         
        LoadI                                  
        JumpFalse    -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $mmgr-remove-prev         
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Jump         -mmgr-remove-process-next 
        Label        -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-next 
        PushD        $mmgr-remove-next         
        LoadI                                  
        JumpFalse    -mmgr-remove-done         
        PushD        $mmgr-remove-prev         
        LoadI                                  
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-done         
        PushD        $mmgr-remove-return       
        LoadI                                  
        Return                                 
        Label        -mem-manager-get-id       
        Exchange                               
        PushI        4                         
        Subtract                               
        LoadI                                  
        Exchange                               
        Return                                 
        DLabel       -$mmgr-diag-jump-table    
        DataD        -$mmgr-diag-test-0        
        DataD        -$mmgr-diag-test-1        
        DataD        -$mmgr-diag-test-2        
        DataD        -$mmgr-diag-test-3        
        DataD        -$mmgr-diag-test-4        
        DLabel       $mmgr-diag-return         
        DataZ        4                         
        Label        -mem-manager-diagnostics  
        PushD        $mmgr-diag-return         
        Exchange                               
        StoreI                                 
        PushI        4                         
        Multiply                               
        PushD        -$mmgr-diag-jump-table    
        Add                                    
        LoadI                                  
        JumpV                                  
        Label        -$mmgr-diag-end           
        PushD        $mmgr-diag-return         
        LoadI                                  
        Return                                 
        Label        -$mmgr-diag-test-0        
        Jump         -$mmgr-diag-end           
        Label        -$mmgr-diag-test-1        
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Jump         -$mmgr-diag-end           
        Label        -$mmgr-diag-test-2        
        Duplicate                              
        PushI        -4                        
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        8                         
        Add                                    
        LoadC                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        9                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Jump         -$mmgr-diag-end           
        Label        -$mmgr-diag-test-3        
        Duplicate                              
        PushI        -4                        
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        8                         
        Add                                    
        LoadC                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        9                         
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-separator   
        Printf                                 
        Duplicate                              
        PushI        13                        
        Add                                    
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Pop                                    
        Jump         -$mmgr-diag-end           
        Label        -$mmgr-diag-test-4        
        Pop                                    
        Pop                                    
        Pop                                    
        Jump         -$mmgr-diag-end           
        DLabel       mmgr-stringPrintFormat    
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $heap-memory              
