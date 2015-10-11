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
        DataZ        28                        
        Label        $$main                    
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% quarters
        PushI        100                       
        StoreC                                 
        PushD        $global-memory-block      
        PushI        1                         
        Add                                    %% Dimes
        DLabel       -str-constant-1           
        DataC        120                       %% "xx"
        DataC        120                       
        DataC        0                         
        PushD        -str-constant-1           
        StoreC                                 
        PushD        $global-memory-block      
        PushI        5                         
        Add                                    %% dimesxddd2_
        DLabel       -str-constant-2           
        DataC        97                        %% "asdasdasd asd asd2"
        DataC        115                       
        DataC        100                       
        DataC        97                        
        DataC        115                       
        DataC        100                       
        DataC        97                        
        DataC        115                       
        DataC        100                       
        DataC        32                        
        DataC        97                        
        DataC        115                       
        DataC        100                       
        DataC        32                        
        DataC        97                        
        DataC        115                       
        DataC        100                       
        DataC        50                        
        DataC        0                         
        PushD        -str-constant-2           
        StoreC                                 
        PushD        $global-memory-block      
        PushI        9                         
        Add                                    %% nickelsD
        PushI        7                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        13                        
        Add                                    %% pennies
        PushI        17                        
        StoreI                                 
        PushD        $global-memory-block      
        PushI        17                        
        Add                                    %% x
        PushI        1                         
        StoreC                                 
        PushD        $global-memory-block      
        PushI        18                        
        Add                                    %% y
        PushI        0                         
        StoreC                                 
        PushD        $global-memory-block      
        PushI        19                        
        Add                                    %% bad
        PushI        23                        
        PushI        10                        
        Multiply                               
        PushI        14                        
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        23                        
        Add                                    %% bad
        PushI        25                        
        StoreI                                 
        PushD        $global-memory-block      
        PushI        27                        
        Add                                    %% quarters
        PushI        122                       
        StoreC                                 
        PushD        $global-memory-block      
        PushI        23                        
        Add                                    %% bad
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        1                         
        Add                                    %% Dimes
        LoadC                                  
        PushD        $print-format-string      
        Printf                                 
        PushD        $global-memory-block      
        PushI        5                         
        Add                                    %% dimesxddd2_
        LoadC                                  
        PushD        $print-format-string      
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        5                         
        Add                                    %% dimesxddd2_
        LoadC                                  
        PushD        $print-format-string      
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        27                        
        Add                                    %% quarters
        LoadC                                  
        PushD        $print-format-character   
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        Halt                                   
